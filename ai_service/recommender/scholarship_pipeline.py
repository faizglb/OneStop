from typing import List
from models import ScholarshipAiRequestModel, ScholarshipRecDtoModel

class ScholarshipRecommendationPipeline:
    @staticmethod
    def get_recommendations(request: ScholarshipAiRequestModel) -> List[ScholarshipRecDtoModel]:
        """
        Computes match eligibility scores, confidence levels, and justifications.
        Rules evaluated:
          - Marks eligibility (CGPA translated to percentage: cgpa * 10)
          - Income limit checking
          - Category / Caste validation
          - Gender matching
          - Special tags (Minority / Disability)
          - Career and College alignments
        """
        recs = []
        
        for scholarship in request.scholarships:
            eligibility_score = 100.0
            reasons = []
            missing = []
            
            # Translate CGPA to Percentage (0-100 scale)
            student_pct = request.current_cgpa * 10.0 if request.current_cgpa <= 10.0 else request.current_cgpa
            
            # ── 1. Academic Marks Check ──
            if scholarship.minimum_marks:
                required = scholarship.minimum_marks
                # Handle CGPA vs Percentage specification (e.g. 7.5 vs 80)
                min_val = required * 10.0 if required <= 10.0 else required
                if student_pct < min_val:
                    eligibility_score -= 30.0
                    missing.append(f"Requires minimum marks of {required}%")
                else:
                    reasons.append("Your academic records exceed the minimum marks cutoff")

            # ── 2. Income Limit Check ──
            if scholarship.income_limit:
                limit = scholarship.income_limit
                if request.family_income > limit:
                    eligibility_score -= 40.0
                    missing.append(f"Family income is above threshold limit of ₹{limit:,.0f}")
                else:
                    reasons.append("Annual family income satisfies financial limits")

            # ── 3. Category / Caste Check ──
            if scholarship.category:
                req_cat = scholarship.category.lower()
                stud_cat = request.category.lower()
                if req_cat != stud_cat and req_cat != "general":
                    eligibility_score -= 30.0
                    missing.append(f"Shortlist limited to {scholarship.category} category students")
                else:
                    reasons.append(f"Matches reservation category specifications ({request.category})")

            # ── 4. Gender Check ──
            if scholarship.gender:
                req_gen = scholarship.gender.lower()
                stud_gen = request.gender.lower()
                if req_gen != stud_gen:
                    eligibility_score -= 50.0
                    missing.append(f"Eligible for {scholarship.gender} students only")
                else:
                    reasons.append(f"Matches target gender specification ({request.gender})")

            # ── 5. Minority & Disability Checks ──
            if scholarship.minority_eligible and not request.is_minority:
                eligibility_score -= 30.0
                missing.append("Requires minority community student status")
            elif scholarship.minority_eligible and request.is_minority:
                reasons.append("Matches minority community eligibility parameters")
                
            if scholarship.disability_eligible and not request.has_disability:
                eligibility_score -= 40.0
                missing.append("Requires physical disability status eligibility")
            elif scholarship.disability_eligible and request.has_disability:
                reasons.append("Matches disability reservation status specifications")

            # ── 6. State Location Check ──
            if scholarship.state:
                req_state = scholarship.state.lower()
                stud_state = request.state.lower()
                if req_state != stud_state:
                    eligibility_score -= 20.0
                    missing.append(f"Requires domicile residence of {scholarship.state}")
                else:
                    reasons.append(f"Matches state residency requirements ({request.state})")

            # ── Score Boundings and Status mapping ──
            final_eligibility = max(0.0, eligibility_score)
            
            if final_eligibility >= 90.0:
                app_status = "ELIGIBLE"
            elif final_eligibility >= 50.0:
                app_status = "PARTIALLY_ELIGIBLE"
            else:
                app_status = "NOT_ELIGIBLE"

            # ── 7. Career & College alignment checks (Confidence Booster) ──
            confidence_score = final_eligibility
            
            # Check if course stream aligns
            course_matched = False
            if scholarship.course:
                req_course = scholarship.course.lower()
                stud_course = request.current_course.lower()
                if req_course in stud_course or stud_course in req_course:
                    confidence_score += 5.0
                    course_matched = True
                    reasons.append(f"Directly supports your course stream ({request.current_course})")

            # Check if student's career recommended path matches scholarship scope
            career_goals_lower = [g.lower() for g in request.career_goals]
            recommended_careers_lower = [c.lower() for c in request.recommended_careers]
            
            if any("science" in g or "engineer" in g or "tech" in g for g in career_goals_lower + recommended_careers_lower):
                if scholarship.course and "science" in scholarship.course.lower() or "b.tech" in scholarship.description.lower():
                    confidence_score += 5.0
                    reasons.append("Aligns with your future career aspirations in Science & Technology")

            final_confidence = min(100.0, max(10.0, confidence_score))

            # Formulate detailed reasons
            if missing:
                missing_txt = " • Missing: " + ", ".join(missing)
            else:
                missing_txt = ""

            reason_text = " • ".join(reasons[:3]) + missing_txt + "."

            recs.append(ScholarshipRecDtoModel(
                scholarshipId=str(scholarship.scholarship_id),
                eligibilityScore=round(final_eligibility, 2),
                confidenceScore=round(final_confidence, 2),
                recommendationReason=reason_text,
                applicationStatus=app_status
            ))

        # Sort by eligibility score descending
        recs.sort(key=lambda r: r.eligibility_score, reverse=True)
        return recs
