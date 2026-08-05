from typing import List, Dict, Any
from models import CollegeAiServiceRequestModel, CollegeRecDtoModel

class CollegeRecommendationPipeline:
    @staticmethod
    def get_recommendations(request: CollegeAiServiceRequestModel) -> List[CollegeRecDtoModel]:
        """
        Computes match compatibility, confidence index, and justifications for all colleges.
        Weights:
          - Cutoffs eligibility: 30%
          - Budget compatibility: 20%
          - Career alignment: 20%
          - Location & hostel: 15%
          - Assessment & academics: 15%
        """
        recs = []
        
        for college in request.colleges:
            # ── 1. Cutoffs Match (30%) ──────────────────────────────────────────
            # Mock or check actual cutoffs. For seed data:
            name = college.college_name.lower()
            rank = request.exam_rank
            exam = request.entrance_exam.lower()
            
            cutoff_score = 0.5 # Default
            
            if "iit" in name:
                # Top tier government cutoffs
                if rank <= 500: cutoff_score = 1.0
                elif rank <= 2000: cutoff_score = 0.8
                elif rank <= 8000: cutoff_score = 0.5
                else: cutoff_score = 0.1
            elif "bits" in name:
                # Private premium merit
                if rank <= 300: cutoff_score = 1.0
                elif rank <= 1000: cutoff_score = 0.7
                else: cutoff_score = 0.2
            elif "nit" in name:
                if rank <= 2000: cutoff_score = 1.0
                elif rank <= 8000: cutoff_score = 0.8
                elif rank <= 20000: cutoff_score = 0.5
                else: cutoff_score = 0.2
            elif "vellore" in name:
                # Private tier 2
                if rank <= 10000: cutoff_score = 1.0
                elif rank <= 50000: cutoff_score = 0.8
                else: cutoff_score = 0.4
            else:
                # DU or others
                if rank <= 5000: cutoff_score = 1.0
                else: cutoff_score = 0.6

            # ── 2. Budget Compatibility (20%) ─────────────────────────────────
            annual_fee = college.annual_fees
            max_budget = request.max_budget
            
            if annual_fee <= max_budget:
                budget_score = 1.0
            else:
                # Scale down proportionally
                budget_score = max(0.1, max_budget / annual_fee)

            # ── 3. Career & Course Stream Alignment (20%) ─────────────────────
            career_goals_lower = [cg.lower() for cg in request.career_goals]
            course_streams = [co.stream.lower() for co in college.courses]
            course_names = [co.course_name.lower() for co in college.courses]
            
            career_score = 0.5
            recommended_course = "General Stream"
            
            # Simple keyword matching to map a course
            if college.courses:
                recommended_course = college.courses[0].course_name
                
            # Align with tech goals
            if any("software" in cg or "developer" in cg or "engineer" in cg or "data" in cg or "ai" in cg for cg in career_goals_lower):
                tech_courses = [c.course_name for c in college.courses if "computer" in c.course_name.lower() or "data" in c.course_name.lower() or "information" in c.course_name.lower()]
                if tech_courses:
                    career_score = 1.0
                    recommended_course = tech_courses[0]
                elif "engineering" in course_streams:
                    career_score = 0.8
            # Align with business goals
            elif any("finance" in cg or "manager" in cg or "business" in cg for cg in career_goals_lower):
                biz_courses = [c.course_name for c in college.courses if "business" in c.course_name.lower() or "commerce" in c.course_name.lower() or "management" in c.course_name.lower()]
                if biz_courses:
                    career_score = 1.0
                    recommended_course = biz_courses[0]
                elif "management" in course_streams:
                    career_score = 0.8

            # ── 4. Location & Hostel Preference (15%) ──────────────────────────
            loc_score = 1.0
            if request.location_preference:
                pref_loc = request.location_preference.lower()
                if pref_loc in college.state.lower() or pref_loc in college.city.lower():
                    loc_score = 1.0
                else:
                    loc_score = 0.5
                    
            hostel_score = 1.0
            if request.hostel_required and not college.hostel_available:
                hostel_score = 0.3
                
            pref_score = (loc_score * 0.6) + (hostel_score * 0.4)

            # ── 5. Assessment & Academics (15%) ────────────────────────────────
            # Higher CGPA matches top NIRF ranking colleges
            nirf = college.nirf_ranking or 100
            cgpa = request.current_cgpa
            
            academic_match = 0.6
            if nirf <= 10 and cgpa >= 8.5: academic_match = 1.0
            elif nirf <= 30 and cgpa >= 7.5: academic_match = 0.9
            elif nirf > 30: academic_match = 0.8

            # ── Weighted Sum calculation ──────────────────────────────────────
            match_score = (
                cutoff_score * 0.30 +
                budget_score * 0.20 +
                career_score * 0.20 +
                pref_score * 0.15 +
                academic_match * 0.15
            )
            
            # Confidence score incorporates NIRF rankings and placement stats
            confidence_score = (match_score * 0.7) + (0.2 * (1.0 - (min(100, nirf) / 100.0))) + (0.1 * (college.average_package / 2000000.0))
            
            # Formulate reasons
            reasons = []
            if cutoff_score >= 0.8:
                reasons.append(f"Highly eligible based on your rank {rank} in {request.entrance_exam}")
            if budget_score >= 0.9:
                reasons.append("Annual fees fit comfortably within your financial budget limits")
            else:
                reasons.append("Fees slightly exceed preferred budget, but offers premium ROI")
                
            if career_score >= 0.9:
                reasons.append(f"Aligns strongly with your career path in {recommended_course}")
                
            if hostel_score >= 0.9 and request.hostel_required:
                reasons.append("Provides excellent on-campus residential facilities")
                
            if college.average_package > 1200000:
                reasons.append(f"Features outstanding placement packages averaging {college.average_package/100000:.1f} LPA")

            reason_text = " • ".join(reasons[:3]) + "."

            recs.append(CollegeRecDtoModel(
                collegeId=str(college.college_id),
                matchScore=round(match_score * 100.0, 2),
                confidenceScore=round(min(1.0, confidence_score) * 100.0, 2),
                recommendationReason=reason_text,
                recommendedCourse=recommended_course
            ))
            
        # Sort by match score descending
        recs.sort(key=lambda r: r.match_score, reverse=True)
        return recs
