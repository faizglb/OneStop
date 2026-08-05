import unittest
from models import AiServiceRequestModel, CollegeAiServiceRequestModel, CollegeProfileDto, CollegeCourseDto, ScholarshipAiRequestModel, ScholarshipProfileDto, EligibilityRuleDto, LearningAiRequestModel, ResourceProfileDto, RoadmapAiRequestModel, ResourceItemDtoModel
from recommender.pipeline import RecommendationPipeline
from recommender.college_pipeline import CollegeRecommendationPipeline
from recommender.scholarship_pipeline import ScholarshipRecommendationPipeline
from recommender.learning_pipeline import LearningRecommendationPipeline
from recommender.roadmap_pipeline import RoadmapGeneratorPipeline

class TestRecommendationPipeline(unittest.TestCase):
    def test_pipeline_computes_recommendations(self):
        req = AiServiceRequestModel(
            educationLevel="Bachelor's",
            currentCourse="B.Tech",
            branch="Computer Science",
            currentYear=3,
            currentCgpa=8.5,
            currentSkills=["Java", "SQL", "Git"],
            interests=["Technology", "Software Development"],
            careerGoals=["Become a Software Engineer"],
            languagesKnown=["English", "Hindi"],
            aptitudeScores={
                "Logical Reasoning": 85.0,
                "Numerical Ability": 75.0,
                "Problem Solving": 90.0,
                "Programming Fundamentals": 95.0,
                "Analytical Thinking": 80.0
            },
            personalityTraits={
                "OPENNESS": 80.0,
                "CONSCIENTIOUSNESS": 90.0,
                "EXTRAVERSION": 45.0,
                "AGREEABLENESS": 70.0,
                "EMOTIONAL_STABILITY": 75.0
            },
            riasecProfile={
                "REALISTIC": 70.0,
                "INVESTIGATIVE": 85.0,
                "ARTISTIC": 30.0,
                "SOCIAL": 40.0,
                "ENTERPRISING": 50.0,
                "CONVENTIONAL": 80.0
            }
        )
        
        recs = RecommendationPipeline.get_recommendations(req)
        self.assertTrue(len(recs) > 0)
        top_rec = recs[0]
        self.assertIsNotNone(top_rec.career_name)
        self.assertTrue(0.0 <= top_rec.confidence_score <= 100.0)

    def test_college_pipeline_computes_recommendations(self):
        college_dto = CollegeProfileDto(
            collegeId="11111111-1111-1111-1111-111111111111",
            collegeName="IIT Delhi",
            collegeType="Government",
            state="Delhi",
            city="New Delhi",
            nirfRanking=2,
            averagePackage=2000000.0,
            annualFees=220000.0,
            hostelAvailable=True,
            courses=[
                CollegeCourseDto(
                    courseId="22222222-2222-2222-2222-222222222221",
                    courseName="Computer Science",
                    stream="Engineering",
                    degree="B.Tech"
                )
            ],
            cutoffs=[]
        )
        
        req = CollegeAiServiceRequestModel(
            currentCgpa=8.5,
            careerGoals=["Software Engineer"],
            aptitudeScores={"Logical Reasoning": 85.0},
            personalityTraits={"OPENNESS": 75.0},
            riasecProfile={"INVESTIGATIVE": 80.0},
            entranceExam="JEE Advanced",
            examRank=250,
            reservationCategory="General",
            maxBudget=300000.0,
            hostelRequired=True,
            locationPreference="Delhi",
            preferredCourse="Computer Science",
            colleges=[college_dto]
        )
        
        recs = CollegeRecommendationPipeline.get_recommendations(req)
        self.assertEqual(len(recs), 1)
        top_rec = recs[0]
        self.assertEqual(top_rec.college_id, "11111111-1111-1111-1111-111111111111")
        self.assertTrue(top_rec.match_score > 80.0)
        self.assertIn("rank 250 in JEE Advanced", top_rec.recommendation_reason)

    def test_scholarship_pipeline_computes_recommendations(self):
        schol_dto = ScholarshipProfileDto(
            scholarshipId="11111111-2222-3333-4444-555555555551",
            scholarshipName="Merit Scholarship",
            provider="MHRD",
            scholarshipType="Government",
            amount=20000.0,
            state="Delhi",
            country="India",
            educationLevel="UNDERGRADUATE",
            course="B.Tech",
            category="General",
            gender="Male",
            minorityEligible=False,
            disabilityEligible=False,
            incomeLimit=400000.0,
            minimumMarks=75.0,
            renewable=True,
            rules=[
                EligibilityRuleDto(
                    ruleType="INCOME",
                    ruleValue="400000",
                    eligibilityRule="Income under 4L"
                )
            ]
        )

        req = ScholarshipAiRequestModel(
            educationLevel="UNDERGRADUATE",
            currentCourse="B.Tech",
            currentCgpa=8.5,
            familyIncome=250000.0,
            category="General",
            gender="Male",
            state="Delhi",
            isMinority=False,
            hasDisability=False,
            careerGoals=["Software Engineer"],
            aptitudeScores={"Logical Reasoning": 80.0},
            personalityTraits={"OPENNESS": 80.0},
            riasecProfile={"INVESTIGATIVE": 80.0},
            recommendedCareers=["Software Developer"],
            recommendedColleges=["IIT Delhi"],
            scholarships=[schol_dto]
        )

        recs = ScholarshipRecommendationPipeline.get_recommendations(req)
        self.assertEqual(len(recs), 1)
        top_rec = recs[0]
        self.assertEqual(top_rec.scholarship_id, "11111111-2222-3333-4444-555555555551")
        self.assertEqual(top_rec.eligibility_score, 100.0)
        self.assertEqual(top_rec.application_status, "ELIGIBLE")
        self.assertIn("income satisfies financial limits", top_rec.recommendation_reason)

    def test_learning_pipeline_computes_recommendations(self):
        res_dto = ResourceProfileDto(
            resourceId="22222222-3333-4444-5555-666666666662",
            title="Java Masterclass",
            description="Complete Java developer course",
            platform="Udemy",
            resourceType="Video Course",
            category="Programming Languages",
            skill="Java",
            difficulty="BEGINNER",
            duration="80 hours",
            rating=4.7,
            isFree=False
        )

        req = LearningAiRequestModel(
            educationLevel="UNDERGRADUATE",
            currentCourse="B.Tech",
            currentCgpa=8.5,
            category="General",
            gender="Male",
            careerGoals=["Software Engineer"],
            aptitudeScores={"Programming Fundamentals": 70.0},
            personalityTraits={"OPENNESS": 80.0},
            riasecProfile={"INVESTIGATIVE": 80.0},
            recommendedCareers=["Software Developer"],
            recommendedColleges=["IIT Delhi"],
            recommendedScholarships=["Merit Scholarship"],
            resources=[res_dto]
        )

        recs = LearningRecommendationPipeline.get_recommendations(req)
        self.assertEqual(len(recs), 1)
        top_rec = recs[0]
        self.assertEqual(top_rec.resource_id, "22222222-3333-4444-5555-666666666662")
        self.assertEqual(top_rec.priority, "HIGH")
        self.assertIn("coding/logical diagnostic results", top_rec.reason)

    def test_roadmap_pipeline_generates_roadmap(self):
        res_item = ResourceItemDtoModel(
            id="22222222-3333-4444-5555-666666666662",
            title="Java Masterclass",
            platform="Udemy",
            category="Programming Languages",
            skill="Java",
            difficulty="BEGINNER",
            url="https://udemy.com/java"
        )

        req = RoadmapAiRequestModel(
            targetCareer="Software Engineer",
            targetCompany="Google",
            duration="60-Day Plan",
            weeklyHours=12,
            startDate="2026-07-25",
            availableResources=[res_item]
        )

        res = RoadmapGeneratorPipeline.generate_roadmap(req)
        self.assertIn("Software Engineer", res.title)
        self.assertEqual(len(res.phases), 8)
        self.assertEqual(len(res.milestones), 3)
        self.assertEqual(res.phases[0].phase_name, "Phase 1: Foundation")

    def test_resume_pipeline_analyzes_resume(self):
        from models import ResumeAiRequestModel
        from recommender.resume_pipeline import ResumeGeneratorPipeline

        req = ResumeAiRequestModel(
            resumeText="Jane Doe resume contents",
            fileName="resume.pdf",
            targetCareer="Software Engineer",
            currentSkills=["Java", "SQL"],
            careerGoals=["Software Engineer"],
            branch="Computer Science",
            educationLevel="UNDERGRADUATE"
        )

        res = ResumeGeneratorPipeline.analyze_resume(req)
        self.assertTrue(res.ats_score > 0)
        self.assertTrue(res.overall_score > 0)
        self.assertEqual(res.page_count, 1)
        self.assertTrue(len(res.sections) > 0)
        self.assertTrue(len(res.keywords) > 0)
        self.assertTrue(len(res.suggestions) > 0)

if __name__ == "__main__":
    unittest.main()
