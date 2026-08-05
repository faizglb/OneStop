import uvicorn
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from models import (
    AiServiceRequestModel, AiServiceResponseModel,
    CollegeAiServiceRequestModel, CollegeAiServiceResponseModel,
    ScholarshipAiRequestModel, ScholarshipAiResponseModel,
    LearningAiRequestModel, LearningAiResponseModel,
    RoadmapAiRequestModel, RoadmapAiResponseModel,
    ResumeAiRequestModel, ResumeAiResponseModel,
    SkillGapAiRequestModel, SkillGapAiResponseModel
)
from recommender.pipeline import RecommendationPipeline
from recommender.college_pipeline import CollegeRecommendationPipeline
from recommender.scholarship_pipeline import ScholarshipRecommendationPipeline
from recommender.learning_pipeline import LearningRecommendationPipeline
from recommender.roadmap_pipeline import RoadmapGeneratorPipeline
from recommender.resume_pipeline import ResumeGeneratorPipeline
from recommender.skill_gap_pipeline import SkillGapPipeline

from chatbot.models import ChatRequest, ChatResponse, InitSessionRequest, InitSessionResponse
from chatbot.rag_pipeline import get_rag_pipeline
from chatbot.memory import get_conversation_memory
from chatbot.ingestion import run_ingestion
from config import Config
import logging

# Setup Logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("ai-service")

app = FastAPI(
    title="ACEA AI Recommendation & Chatbot Service",
    description="Microservice to compute career recommendations and power the enterprise AI Career Chatbot.",
    version="1.1.0"
)

# Setup CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.on_event("startup")
def startup_event():
    logger.info("Initializing ACEA AI Service...")
    try:
        # Automatically ingest knowledge base into vector store if empty
        stats = run_ingestion(force_reingest=False)
        logger.info(f"Knowledge Base Ingestion status: {stats}")
    except Exception as e:
        logger.warning(f"Could not complete knowledge base ingestion at startup: {e}")

@app.get("/health")
def health_check():
    return {"status": "UP", "service": "ACEA AI Recommendation & Chatbot Engine"}

@app.post("/api/recommend", response_model=AiServiceResponseModel)
def recommend_careers(request: AiServiceRequestModel):
    logger.info(f"Received recommendation request for student branch={request.branch}, CGPA={request.current_cgpa}")
    try:
        recommendations = RecommendationPipeline.get_recommendations(request)
        logger.info(f"Generated {len(recommendations)} career recommendations successfully.")
        return AiServiceResponseModel(recommendations=recommendations)
    except Exception as e:
        logger.error("Exception occurred during recommendation generation", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Failed to generate recommendations: {str(e)}")

@app.post("/api/recommend-colleges", response_model=CollegeAiServiceResponseModel)
def recommend_colleges(request: CollegeAiServiceRequestModel):
    logger.info(f"Received college recommendation request for exam={request.entrance_exam}, rank={request.exam_rank}")
    try:
        recommendations = CollegeRecommendationPipeline.get_recommendations(request)
        logger.info(f"Generated {len(recommendations)} college recommendations successfully.")
        return CollegeAiServiceResponseModel(recommendations=recommendations)
    except Exception as e:
        logger.error("Exception occurred during college recommendation generation", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Failed to generate college recommendations: {str(e)}")

@app.post("/api/recommend-scholarships", response_model=ScholarshipAiResponseModel)
def recommend_scholarships(request: ScholarshipAiRequestModel):
    logger.info(f"Received scholarship recommendation request for student CGPA={request.current_cgpa}, income={request.family_income}")
    try:
        recommendations = ScholarshipRecommendationPipeline.get_recommendations(request)
        logger.info(f"Generated {len(recommendations)} scholarship recommendations successfully.")
        return ScholarshipAiResponseModel(recommendations=recommendations)
    except Exception as e:
        logger.error("Exception occurred during scholarship recommendation generation", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Failed to generate scholarship recommendations: {str(e)}")

@app.post("/api/recommend-resources", response_model=LearningAiResponseModel)
def recommend_resources(request: LearningAiRequestModel):
    logger.info(f"Received learning recommendation request for student education level={request.education_level}, course={request.current_course}")
    try:
        recommendations = LearningRecommendationPipeline.get_recommendations(request)
        logger.info(f"Generated {len(recommendations)} learning recommendations successfully.")
        return LearningAiResponseModel(recommendations=recommendations)
    except Exception as e:
        logger.error("Exception occurred during learning recommendation generation", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Failed to generate learning recommendations: {str(e)}")

@app.post("/api/generate-roadmap", response_model=RoadmapAiResponseModel)
def generate_roadmap(request: RoadmapAiRequestModel):
    logger.info(f"Received roadmap generation request for target career={request.target_career}, duration={request.duration}")
    try:
        roadmap = RoadmapGeneratorPipeline.generate_roadmap(request)
        logger.info(f"Generated roadmap '{roadmap.title}' successfully with {len(roadmap.phases)} phases.")
        return roadmap
    except Exception as e:
        logger.error("Exception occurred during roadmap generation", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Failed to generate roadmap: {str(e)}")

@app.post("/api/analyze-resume", response_model=ResumeAiResponseModel)
def analyze_resume(request: ResumeAiRequestModel):
    logger.info(f"Received resume analysis request for file={request.file_name}, target career={request.target_career}")
    try:
        analysis = ResumeGeneratorPipeline.analyze_resume(request)
        logger.info(f"Completed resume analysis for '{request.file_name}' successfully.")
        return analysis
    except Exception as e:
        logger.error("Exception occurred during resume analysis", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Failed to analyze resume: {str(e)}")

@app.post("/api/analyze-skill-gap", response_model=SkillGapAiResponseModel)
def analyze_skill_gap(request: SkillGapAiRequestModel):
    logger.info(f"Received skill gap analysis request for student_id={request.student_id}, target_career={request.target_career}")
    try:
        analysis = SkillGapPipeline.analyze_skill_gap(request)
        logger.info(f"Completed skill gap analysis for target career '{request.target_career}' successfully with readiness {analysis.overall_readiness_score}%.")
        return analysis
    except Exception as e:
        logger.error("Exception occurred during skill gap analysis", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Failed to analyze skill gap: {str(e)}")

# ── Sprint 11: AI Career Chatbot Endpoints ──────────────────────────────

@app.post("/api/chat", response_model=ChatResponse)
def chat_with_advisor(request: ChatRequest):
    logger.info(f"Received chat request for session={request.session_id}, student={request.student_id}")
    try:
        pipeline = get_rag_pipeline()
        response = pipeline.execute(request)
        logger.info(f"Generated chat response via agent '{response.agent_used}' (confidence: {response.confidence_score}).")
        return response
    except Exception as e:
        logger.error("Exception occurred during chat processing", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Chat processing failed: {str(e)}")

@app.post("/api/chat/session/init", response_model=InitSessionResponse)
def init_chat_session(request: InitSessionRequest):
    logger.info(f"Initializing memory session={request.session_id}")
    try:
        memory = get_conversation_memory()
        memory.get_or_create_session(request.session_id, request.student_id)
        return InitSessionResponse(session_id=request.session_id)
    except Exception as e:
        logger.error("Failed to init chat session", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

@app.delete("/api/chat/session/{session_id}")
def delete_chat_session(session_id: str):
    logger.info(f"Deleting memory session={session_id}")
    try:
        memory = get_conversation_memory()
        success = memory.delete_session(session_id)
        return {"status": "SUCCESS" if success else "NOT_FOUND", "session_id": session_id}
    except Exception as e:
        logger.error("Failed to delete session", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    uvicorn.run("main:app", host=Config.HOST, port=Config.PORT, reload=True)


