import api from './api';
import {
  SkillGapReport,
  SkillGapAnalyzeRequest,
  SkillGapItem,
  ReadinessBreakdown
} from '@/types/skillGap';

export const skillGapService = {
  analyze: async (data: SkillGapAnalyzeRequest): Promise<SkillGapReport> => {
    const res = await api.post<SkillGapReport>('/skill-gap/analyze', data);
    return res.data;
  },

  getLatestReport: async (): Promise<SkillGapReport> => {
    const res = await api.get<SkillGapReport>('/skill-gap/report');
    return res.data;
  },

  getReportHistory: async (): Promise<SkillGapReport[]> => {
    const res = await api.get<SkillGapReport[]>('/skill-gap/history');
    return res.data;
  },

  getMissingSkills: async (): Promise<SkillGapItem[]> => {
    const res = await api.get<SkillGapItem[]>('/skill-gap/missing-skills');
    return res.data;
  },

  getReadinessBreakdown: async (): Promise<ReadinessBreakdown> => {
    const res = await api.get<ReadinessBreakdown>('/skill-gap/readiness');
    return res.data;
  },

  regenerate: async (data: SkillGapAnalyzeRequest): Promise<SkillGapReport> => {
    const res = await api.post<SkillGapReport>('/skill-gap/regenerate', data);
    return res.data;
  }
};
