import os

class Config:
    PORT = int(os.getenv("PORT", 8000))
    HOST = os.getenv("HOST", "0.0.0.0")

    # Recommendation Weights (Must sum to 1.0 or we normalize them)
    # Specified: 40% Assessment Scores, 30% Skills, 20% Interests, 10% Career Goals
    WEIGHT_ASSESSMENTS = float(os.getenv("WEIGHT_ASSESSMENTS", 0.40))
    WEIGHT_SKILLS = float(os.getenv("WEIGHT_SKILLS", 0.30))
    WEIGHT_INTERESTS = float(os.getenv("WEIGHT_INTERESTS", 0.20))
    WEIGHT_GOALS = float(os.getenv("WEIGHT_GOALS", 0.10))

    # Assessment internals weights
    # 50% Aptitude, 25% Personality, 25% RIASEC Interest Assessment
    WEIGHT_APTITUDE = 0.50
    WEIGHT_PERSONALITY = 0.25
    WEIGHT_RIASEC = 0.25
