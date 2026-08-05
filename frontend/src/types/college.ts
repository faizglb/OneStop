export interface CourseDto {
  id: string;
  courseName: string;
  stream: string;
  duration: number;
  degree: string;
  eligibility: string;
  entranceExam: string;
  seatIntake: number;
}

export interface FacilityDto {
  id: string;
  facilityName: string;
  description: string;
}

export interface CollegeResponse {
  id: string;
  collegeName: string;
  collegeType: string;
  ownership: string;
  state: string;
  district: string;
  city: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
  nirfRanking: number | null;
  naacGrade: string | null;
  aicteApproved: boolean;
  ugcApproved: boolean;
  establishedYear: number;
  website: string | null;
  email: string | null;
  phone: string | null;
  averagePackage: number;
  highestPackage: number;
  medianPackage: number;
  placementPercentage: number;
  hostelAvailable: boolean;
  girlsHostel: boolean;
  boysHostel: boolean;
  totalFees: number;
  annualFees: number;
  campusArea: number;
  description: string | null;
  isBookmarked: boolean;
  courses: CourseDto[];
  facilities: FacilityDto[];
}

export interface CompareItem {
  id: string;
  collegeName: string;
  collegeType: string;
  ownership: string;
  state: string;
  city: string;
  nirfRanking: number | null;
  naacGrade: string | null;
  averagePackage: number;
  highestPackage: number;
  placementPercentage: number;
  annualFees: number;
  totalFees: number;
  campusArea: number;
  hostelAvailable: boolean;
  girlsHostel: boolean;
  boysHostel: boolean;
  facilities: string[];
  courseNames: string[];
}

export interface CollegeCompareResponse {
  colleges: CompareItem[];
}

export interface CollegeRecommendationResponse {
  id: string;
  collegeId: string;
  collegeName: string;
  state: string;
  city: string;
  nirfRanking: number | null;
  averagePackage: number;
  annualFees: number;
  matchScore: number;
  confidenceScore: number;
  recommendationReason: string;
  recommendedCourse: string;
  createdAt: string;
}
