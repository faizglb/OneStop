export interface EligibilityDto {
  id: string;
  eligibilityRule: string;
  ruleType: string;
  ruleValue: string;
}

export interface ScholarshipResponse {
  id: string;
  scholarshipName: string;
  provider: String;
  scholarshipType: string;
  description: string;
  officialWebsite: string | null;
  applicationLink: string | null;
  applicationMode: string;
  amount: number;
  currency: string;
  lastDate: string;
  state: string | null;
  country: string;
  educationLevel: string;
  course: string | null;
  category: string | null;
  gender: string | null;
  minorityEligible: boolean;
  disabilityEligible: boolean;
  incomeLimit: number | null;
  minimumMarks: number | null;
  entranceExam: string | null;
  renewable: boolean;
  requiredDocuments: string;
  status: string;
  isBookmarked: boolean;
  applicationStatus: string | null; // BOOKMARKED | APPLIED | SHORTLISTED | COMPLETED
  eligibilities: EligibilityDto[];
}

export interface ScholarshipApplicationHistoryResponse {
  id: string;
  scholarshipId: string;
  scholarshipName: string;
  provider: string;
  amount: number;
  lastDate: string;
  status: string; // BOOKMARKED | APPLIED | SHORTLISTED | COMPLETED
  notes: string | null;
  reminderDate: string | null;
  updatedAt: string;
}

export interface ScholarshipRecommendationResponse {
  id: string;
  scholarshipId: string;
  scholarshipName: string;
  provider: string;
  scholarshipType: string;
  amount: number;
  lastDate: string;
  requiredDocuments: string;
  officialWebsite: string;
  renewable: boolean;
  eligibilityScore: number;
  confidenceScore: number;
  recommendationReason: string;
  applicationStatus: string; // ELIGIBLE | PARTIALLY_ELIGIBLE | NOT_ELIGIBLE
  createdAt: string;
}
