import api from './api';

export interface StudentProfileRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  phoneNumber: string;
  profilePhoto?: string;
  country: string;
  state: string;
  district: string;
  city: string;
  address: string;
  pinCode: string;
  schoolName: string;
  collegeName: string;
  currentCourse: string;
  branch: string;
  currentYear: number;
  educationLevel: string;
  tenthPercentage: number;
  twelfthPercentage: number;
  graduationCgpa?: number;
  currentCgpa: number;
  preferredStudyMode: string;
  familyIncome: number;
  category: string;
  caste: string;
  religion?: string;
  disabilityStatus: string;
  languagesKnown: string[];
  careerGoal: string;
  bio: string;
  linkedinUrl?: string;
  githubUrl?: string;
  portfolioUrl?: string;
  resumeUrl?: string;
}

export interface SkillRequest {
  skillName: string;
  skillLevel: string;
  experienceMonths: number;
}

export interface InterestRequest {
  interestName: string;
}

export interface GoalRequest {
  goalTitle: string;
  goalDescription: string;
  targetYear: number;
  priority: string;
}

export interface ProjectRequest {
  projectTitle: string;
  description: string;
  techStack: string[];
  githubLink?: string;
  liveLink?: string;
}

export interface CertificationRequest {
  certificateName: string;
  platform: string;
  issueDate: string;
  certificateUrl: string;
}

export const profileService = {
  getProfile: async () => {
    const { data } = await api.get('/profile');
    return data.data;
  },

  saveProfile: async (profileData: StudentProfileRequest) => {
    const { data } = await api.put('/profile', profileData);
    return data.data;
  },

  deleteProfile: async () => {
    const { data } = await api.delete('/profile');
    return data.data;
  },

  // Skills
  addSkill: async (skill: SkillRequest) => {
    const { data } = await api.post('/skills', skill);
    return data.data;
  },
  updateSkill: async (id: string, skill: SkillRequest) => {
    const { data } = await api.put(`/skills/${id}`, skill);
    return data.data;
  },
  deleteSkill: async (id: string) => {
    const { data } = await api.delete(`/skills/${id}`);
    return data.data;
  },

  // Interests
  addInterest: async (interest: InterestRequest) => {
    const { data } = await api.post('/interests', interest);
    return data.data;
  },
  deleteInterest: async (id: string) => {
    const { data } = await api.delete(`/interests/${id}`);
    return data.data;
  },

  // Goals
  addGoal: async (goal: GoalRequest) => {
    const { data } = await api.post('/goals', goal);
    return data.data;
  },
  updateGoal: async (id: string, goal: GoalRequest) => {
    const { data } = await api.put(`/goals/${id}`, goal);
    return data.data;
  },
  deleteGoal: async (id: string) => {
    const { data } = await api.delete(`/goals/${id}`);
    return data.data;
  },

  // Projects
  addProject: async (project: ProjectRequest) => {
    const { data } = await api.post('/projects', project);
    return data.data;
  },
  updateProject: async (id: string, project: ProjectRequest) => {
    const { data } = await api.put(`/projects/${id}`, project);
    return data.data;
  },
  deleteProject: async (id: string) => {
    const { data } = await api.delete(`/projects/${id}`);
    return data.data;
  },

  // Certificates
  addCertificate: async (certificate: CertificationRequest) => {
    const { data } = await api.post('/certificates', certificate);
    return data.data;
  },
  deleteCertificate: async (id: string) => {
    const { data } = await api.delete(`/certificates/${id}`);
    return data.data;
  },

  // Upload helpers
  uploadProfilePhoto: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const { data } = await api.post('/upload/profile-photo', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data.data;
  },

  uploadResume: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const { data } = await api.post('/upload/resume', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data.data;
  },

  uploadCertificateFile: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const { data } = await api.post('/upload/certificate', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data.data;
  },
};
