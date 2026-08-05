import unittest
from models import SkillGapAiRequestModel
from recommender.skill_gap_pipeline import SkillGapPipeline

class TestSkillGapPipeline(unittest.TestCase):

    def test_analyze_skill_gap(self):
        req = SkillGapAiRequestModel(
            studentId="test-student-123",
            targetCareer="Software Engineer",
            currentSkills=["Java", "SQL", "Git"],
            profileProjects=["E-Commerce Microservices Application"],
            profileCertifications=["AWS Certified Developer"],
            resumeKeywords=["Java", "Spring Boot", "Git"],
            resumeAtsScore=82.5,
            assessmentAptitudeScores={"Logical Reasoning": 85.0, "Problem Solving": 90.0}
        )

        res = SkillGapPipeline.analyze_skill_gap(req)

        self.assertEqual(res.target_career, "Software Engineer")
        self.assertGreater(res.overall_readiness_score, 0)
        self.assertLessEqual(res.overall_readiness_score, 100)
        self.assertTrue(len(res.skill_gaps) > 0)
        self.assertTrue(len(res.improvement_recommendations) > 0)
        self.assertIsNotNone(res.visual_analytics)
        self.assertTrue(len(res.smart_insights) > 0)

if __name__ == "__main__":
    unittest.main()
