import api from './api';

export interface ResumeAnalysisRequest {
  fileUrl: string;
  fileName: string;
  fileType: 'PDF' | 'DOCX';
  targetCareer?: string;
}

export interface ResumeScoreBreakdown {
  keywordScore: number;
  sectionScore: number;
  formattingScore: number;
  experienceScore: number;
  educationScore: number;
  grammarScore: number;
}

export interface ResumeSection {
  sectionName: string;
  detected: boolean;
  quality: 'LOW' | 'MEDIUM' | 'HIGH';
  contentSnippet?: string;
}

export interface ResumeKeyword {
  keyword: string;
  category: string;
  found: boolean;
  frequency: number;
  importance: 'HIGH' | 'MEDIUM' | 'LOW';
}

export interface ResumeSuggestion {
  category: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  title: string;
  description: string;
  example?: string;
}

export interface ResumeAnalysisResponse {
  id: string;
  fileName: string;
  fileUrl: string;
  fileType: 'PDF' | 'DOCX';
  wordCount: number;
  pageCount: number;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  atsScore: number;
  overallScore: number;
  targetCareer?: string;
  createdAt: string;
  updatedAt: string;
  scoreBreakdown: ResumeScoreBreakdown;
  sections: ResumeSection[];
  keywords: ResumeKeyword[];
  suggestions: ResumeSuggestion[];
}

export interface ResumeAnalysisSummaryResponse {
  id: string;
  fileName: string;
  fileType: 'PDF' | 'DOCX';
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  atsScore: number;
  overallScore: number;
  targetCareer?: string;
  wordCount: number;
  pageCount: number;
  createdAt: string;
}

export const resumeService = {
  analyzeResume: async (request: ResumeAnalysisRequest): Promise<ResumeAnalysisResponse> => {
    const { data } = await api.post('/resume-analysis/analyze', request);
    return data.data;
  },

  getAnalysisHistory: async (page = 0, size = 10): Promise<{ content: ResumeAnalysisSummaryResponse[]; totalPages: number; totalElements: number }> => {
    const { data } = await api.get('/resume-analysis', {
      params: { page, size }
    });
    return data.data;
  },

  getLatestAnalysis: async (): Promise<ResumeAnalysisResponse> => {
    const { data } = await api.get('/resume-analysis/latest');
    return data.data;
  },

  getAnalysisById: async (id: string): Promise<ResumeAnalysisResponse> => {
    const { data } = await api.get(`/resume-analysis/${id}`);
    return data.data;
  },

  deleteAnalysis: async (id: string): Promise<void> => {
    await api.delete(`/resume-analysis/${id}`);
  }
};
