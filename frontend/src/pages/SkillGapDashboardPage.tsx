import React, { useState, useEffect } from 'react';
import { 
  Target, 
  TrendingUp, 
  BrainCircuit, 
  Award, 
  Code, 
  BookOpen, 
  Briefcase, 
  CheckCircle2, 
  AlertTriangle, 
  RefreshCw, 
  Sparkles, 
  Clock, 
  Layers, 
  ChevronRight,
  BarChart3,
  Flame,
  Lightbulb
} from 'lucide-react';
import { toast } from 'react-hot-toast';
import { skillGapService } from '@/services/skillGapService';
import { SkillGapReport, SkillGapItem } from '@/types/skillGap';

export default function SkillGapDashboardPage() {
  const [report, setReport] = useState<SkillGapReport | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [targetCareer, setTargetCareer] = useState<string>('Software Engineer');
  const [analyzing, setAnalyzing] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<'overview' | 'skills' | 'matrix' | 'timeline' | 'recommendations'>('overview');
  const [skillFilter, setSkillFilter] = useState<'ALL' | 'MISSING' | 'WEAK' | 'STRONG'>('ALL');

  useEffect(() => {
    fetchLatestReport();
  }, []);

  const fetchLatestReport = async () => {
    setLoading(true);
    try {
      const data = await skillGapService.getLatestReport();
      setReport(data);
      if (data.targetCareer) {
        setTargetCareer(String(data.targetCareer));
      }
    } catch (err) {
      // If no report exists yet, auto-run initial analysis
      runAnalysis('Software Engineer');
    } finally {
      setLoading(false);
    }
  };

  const runAnalysis = async (career: string) => {
    setAnalyzing(true);
    try {
      const data = await skillGapService.analyze({ targetCareer: career });
      setReport(data);
      toast.success(`Skill Gap Analysis generated for ${career}!`);
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to analyze skill gap. Please try again.');
    } finally {
      setAnalyzing(false);
      setLoading(false);
    }
  };

  const handleCareerSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!targetCareer.trim()) return;
    runAnalysis(targetCareer);
  };

  const filteredSkills = report?.skillGaps?.filter(s => {
    if (skillFilter === 'ALL') return true;
    return s.status === skillFilter;
  }) || [];

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center p-6">
        <div className="flex flex-col items-center space-y-4">
          <RefreshCw className="w-12 h-12 text-indigo-500 animate-spin" />
          <p className="text-lg font-medium text-slate-300">Computing AI Skill Gap & Readiness Metrics...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 md:p-10">
      {/* Header Banner */}
      <div className="max-w-7xl mx-auto space-y-8">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 border-b border-slate-800 pb-6">
          <div>
            <div className="flex items-center space-x-3">
              <div className="p-2.5 bg-indigo-500/10 rounded-xl border border-indigo-500/20 text-indigo-400">
                <BrainCircuit className="w-8 h-8" />
              </div>
              <div>
                <h1 className="text-3xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-400">
                  AI Skill Gap Engine
                </h1>
                <p className="text-slate-400 text-sm mt-1">
                  Targeted analysis comparing your abilities with market expectations for <span className="text-indigo-300 font-semibold">{report?.targetCareer || targetCareer}</span>
                </p>
              </div>
            </div>
          </div>

          {/* Career Switcher / Re-Analyze */}
          <form onSubmit={handleCareerSubmit} className="flex items-center space-x-2">
            <div className="relative">
              <input
                type="text"
                value={targetCareer}
                onChange={(e) => setTargetCareer(e.target.value)}
                placeholder="Target Career (e.g., Data Scientist)"
                className="bg-slate-900/90 border border-slate-700/80 text-slate-200 text-sm rounded-xl px-4 py-2.5 w-64 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <button
              type="submit"
              disabled={analyzing}
              className="flex items-center space-x-2 bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-medium text-sm px-4 py-2.5 rounded-xl transition-all shadow-lg shadow-indigo-500/20 disabled:opacity-50"
            >
              <RefreshCw className={`w-4 h-4 ${analyzing ? 'animate-spin' : ''}`} />
              <span>{analyzing ? 'Analyzing...' : 'Analyze'}</span>
            </button>
          </form>
        </div>

        {/* Smart Insights Banner */}
        {report?.smartInsights && report.smartInsights.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {report.smartInsights.map((insight, idx) => (
              <div
                key={idx}
                className={`p-4 rounded-2xl border backdrop-blur-md flex items-start space-x-3 ${
                  insight.insightType === 'POSITIVE'
                    ? 'bg-emerald-500/5 border-emerald-500/20 text-emerald-300'
                    : insight.insightType === 'WARNING'
                    ? 'bg-amber-500/5 border-amber-500/20 text-amber-300'
                    : insight.insightType === 'ACTIONABLE'
                    ? 'bg-indigo-500/5 border-indigo-500/20 text-indigo-300'
                    : 'bg-purple-500/5 border-purple-500/20 text-purple-300'
                }`}
              >
                <div className="p-2 rounded-lg bg-slate-900/50 mt-0.5">
                  <Lightbulb className="w-5 h-5" />
                </div>
                <div>
                  <h4 className="text-sm font-semibold">{insight.title}</h4>
                  <p className="text-xs text-slate-300 mt-1 leading-relaxed">{insight.description}</p>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Main Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Overall Readiness Score Card */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 relative overflow-hidden flex flex-col justify-between">
            <div className="absolute top-0 right-0 p-8 opacity-10">
              <Target className="w-32 h-32 text-indigo-400" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Job Readiness</span>
              <h3 className="text-4xl font-extrabold text-white mt-2">
                {report?.overallReadinessScore || 0}%
              </h3>
              <p className="text-slate-400 text-xs mt-2">
                Overall score out of 100 benchmarked against industry standards.
              </p>
            </div>
            <div className="mt-6 w-full bg-slate-800 rounded-full h-3 overflow-hidden">
              <div
                className="bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 h-full rounded-full transition-all duration-1000"
                style={{ width: `${report?.overallReadinessScore || 0}%` }}
              />
            </div>
          </div>

          {/* Skill Gap Percentage Card */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 relative overflow-hidden flex flex-col justify-between">
            <div className="absolute top-0 right-0 p-8 opacity-10">
              <Flame className="w-32 h-32 text-pink-400" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase tracking-wider text-pink-400">Total Skill Gap</span>
              <h3 className="text-4xl font-extrabold text-white mt-2">
                {report?.overallGapPercentage || 0}%
              </h3>
              <p className="text-slate-400 text-xs mt-2">
                Skill gap remaining to reach full job competency.
              </p>
            </div>
            <div className="mt-6 flex items-center justify-between text-xs text-slate-400 border-t border-slate-800/80 pt-3">
              <span>Status: <span className="text-pink-300 font-medium">In Progress</span></span>
              <span>Gap Level: <span className="text-amber-400 font-medium">Moderate</span></span>
            </div>
          </div>

          {/* Estimated Job Readiness Months */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 relative overflow-hidden flex flex-col justify-between">
            <div className="absolute top-0 right-0 p-8 opacity-10">
              <Clock className="w-32 h-32 text-purple-400" />
            </div>
            <div>
              <span className="text-xs font-semibold uppercase tracking-wider text-purple-400">Estimated Job Ready</span>
              <h3 className="text-4xl font-extrabold text-white mt-2">
                ~{report?.estimatedJobReadinessMonths || 0} <span className="text-lg font-normal text-slate-400">Months</span>
              </h3>
              <p className="text-slate-400 text-xs mt-2">
                Based on ~10 hrs/week structured learning roadmap.
              </p>
            </div>
            <div className="mt-6 flex items-center space-x-2 text-xs text-indigo-300 bg-indigo-500/10 p-2.5 rounded-xl border border-indigo-500/20">
              <Sparkles className="w-4 h-4 flex-shrink-0" />
              <span>Completing recommended capstones speeds up readiness by 2x.</span>
            </div>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="flex border-b border-slate-800 space-x-8">
          {[
            { id: 'overview', label: 'Readiness Breakdown' },
            { id: 'skills', label: 'Skill Gap Heatmap' },
            { id: 'matrix', label: 'Priority Matrix' },
            { id: 'timeline', label: 'Learning Timeline' },
            { id: 'recommendations', label: 'Improvement Plan' }
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`pb-4 font-medium text-sm transition-all border-b-2 ${
                activeTab === tab.id
                  ? 'border-indigo-500 text-indigo-400'
                  : 'border-transparent text-slate-400 hover:text-slate-200'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* TAB 1: READINESS BREAKDOWN & RADAR */}
        {activeTab === 'overview' && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Multi-category Progress Bars */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 space-y-6">
              <h3 className="text-lg font-bold text-slate-200 flex items-center space-x-2">
                <BarChart3 className="w-5 h-5 text-indigo-400" />
                <span>Multi-Category Readiness Scores</span>
              </h3>

              <div className="space-y-4">
                {[
                  { label: 'Technical Readiness', score: report?.readinessBreakdown?.technicalReadiness || 0, icon: Code, color: 'bg-indigo-500' },
                  { label: 'Coding & Algorithmic Readiness', score: report?.readinessBreakdown?.codingReadiness || 0, icon: BrainCircuit, color: 'bg-purple-500' },
                  { label: 'Project & Capstone Readiness', score: report?.readinessBreakdown?.projectReadiness || 0, icon: Briefcase, color: 'bg-pink-500' },
                  { label: 'Interview Readiness', score: report?.readinessBreakdown?.interviewReadiness || 0, icon: TrendingUp, color: 'bg-emerald-500' },
                  { label: 'Resume & ATS Readiness', score: report?.readinessBreakdown?.resumeReadiness || 0, icon: BookOpen, color: 'bg-amber-500' },
                  { label: 'Soft Skills Readiness', score: report?.readinessBreakdown?.softSkillReadiness || 0, icon: Award, color: 'bg-cyan-500' }
                ].map((item, idx) => (
                  <div key={idx} className="space-y-1.5">
                    <div className="flex justify-between text-xs font-medium">
                      <span className="flex items-center space-x-2 text-slate-300">
                        <item.icon className="w-4 h-4 text-slate-400" />
                        <span>{item.label}</span>
                      </span>
                      <span className="text-indigo-400 font-bold">{item.score}%</span>
                    </div>
                    <div className="w-full bg-slate-800 rounded-full h-2.5 overflow-hidden">
                      <div
                        className={`${item.color} h-full rounded-full transition-all duration-700`}
                        style={{ width: `${item.score}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Radar / Spider Chart Simulation */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 flex flex-col justify-between">
              <h3 className="text-lg font-bold text-slate-200 flex items-center space-x-2">
                <Target className="w-5 h-5 text-purple-400" />
                <span>Skill Competency Spider Chart</span>
              </h3>

              <div className="my-6 flex items-center justify-center relative h-64">
                {/* SVG Radar Representation */}
                <svg className="w-full h-full max-w-xs" viewBox="0 0 200 200">
                  <polygon points="100,20 170,60 170,140 100,180 30,140 30,60" fill="none" stroke="#334155" strokeWidth="1.5" />
                  <polygon points="100,45 145,72 145,128 100,155 55,128 55,72" fill="none" stroke="#334155" strokeWidth="1" />
                  {/* Student Score Polygon */}
                  <polygon
                    points="100,35 155,68 150,132 100,165 45,130 40,65"
                    fill="rgba(99, 102, 241, 0.25)"
                    stroke="#818cf8"
                    strokeWidth="2.5"
                  />
                </svg>
                <div className="absolute top-2 text-[10px] text-indigo-400 font-semibold">Technical</div>
                <div className="absolute right-2 top-14 text-[10px] text-purple-400 font-semibold">Coding</div>
                <div className="absolute right-2 bottom-14 text-[10px] text-pink-400 font-semibold">Projects</div>
                <div className="absolute bottom-2 text-[10px] text-emerald-400 font-semibold">Interview</div>
                <div className="absolute left-2 bottom-14 text-[10px] text-amber-400 font-semibold">Resume</div>
                <div className="absolute left-2 top-14 text-[10px] text-cyan-400 font-semibold">Soft Skills</div>
              </div>

              <div className="flex items-center justify-center space-x-6 text-xs text-slate-400">
                <span className="flex items-center space-x-2">
                  <span className="w-3 h-3 bg-indigo-500 rounded-sm inline-block"></span>
                  <span>Your Ability</span>
                </span>
                <span className="flex items-center space-x-2">
                  <span className="w-3 h-3 border border-slate-600 rounded-sm inline-block"></span>
                  <span>Required Level</span>
                </span>
              </div>
            </div>
          </div>
        )}

        {/* TAB 2: SKILL GAP HEATMAP */}
        {activeTab === 'skills' && (
          <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 space-y-6">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
              <h3 className="text-lg font-bold text-slate-200">Skill Gap Heatmap & Analysis</h3>
              <div className="flex items-center space-x-2 text-xs">
                {['ALL', 'MISSING', 'WEAK', 'STRONG'].map(filter => (
                  <button
                    key={filter}
                    onClick={() => setSkillFilter(filter as any)}
                    className={`px-3 py-1.5 rounded-xl font-medium transition-all ${
                      skillFilter === filter
                        ? 'bg-indigo-600 text-white'
                        : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
                    }`}
                  >
                    {filter}
                  </button>
                ))}
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {filteredSkills.map((skill, idx) => (
                <div key={idx} className="bg-slate-950/80 border border-slate-800/80 rounded-2xl p-4 flex flex-col justify-between space-y-3">
                  <div className="flex items-start justify-between">
                    <div>
                      <h4 className="text-sm font-bold text-white">{skill.skillName}</h4>
                      <span className="text-[10px] text-slate-400 uppercase tracking-wider">{skill.category}</span>
                    </div>
                    <span className={`text-[10px] font-semibold px-2.5 py-1 rounded-full ${
                      skill.status === 'MISSING' ? 'bg-red-500/10 text-red-400 border border-red-500/20' :
                      skill.status === 'WEAK' ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20' :
                      'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                    }`}>
                      {skill.status}
                    </span>
                  </div>

                  <div className="grid grid-cols-2 gap-2 text-xs bg-slate-900/60 p-2.5 rounded-xl text-slate-300">
                    <div>Current Level: <span className="font-semibold text-indigo-300">{skill.currentLevel}</span></div>
                    <div>Required Level: <span className="font-semibold text-purple-300">{skill.requiredLevel}</span></div>
                    <div>Gap: <span className="font-semibold text-pink-300">{skill.gapPercentage}%</span></div>
                    <div>Est. Time: <span className="font-semibold text-cyan-300">{skill.estimatedLearningHours} hrs</span></div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* TAB 3: PRIORITY MATRIX */}
        {activeTab === 'matrix' && (
          <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 space-y-6">
            <h3 className="text-lg font-bold text-slate-200">Skill Priority Matrix (Importance vs Effort)</h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {[
                { title: '⚡ Quick Wins (High Importance, Low Effort)', quadrant: 'Quick Win', color: 'border-emerald-500/30 bg-emerald-500/5' },
                { title: '🎯 Strategic Investments (High Importance, High Effort)', quadrant: 'Strategic Investment', color: 'border-indigo-500/30 bg-indigo-500/5' },
                { title: '🔹 Secondary Focus (Low Importance, Low Effort)', quadrant: 'Secondary', color: 'border-purple-500/30 bg-purple-500/5' },
                { title: '⏳ Low Priority (Low Importance, High Effort)', quadrant: 'Low Priority', color: 'border-slate-700 bg-slate-950/40' }
              ].map((q, idx) => (
                <div key={idx} className={`p-5 rounded-2xl border ${q.color} space-y-3`}>
                  <h4 className="text-sm font-bold text-slate-200">{q.title}</h4>
                  <div className="space-y-2">
                    {report?.visualAnalytics?.priorityMatrix
                      ?.filter(p => p.quadrant === q.quadrant)
                      .map((item, i) => (
                        <div key={i} className="flex items-center justify-between text-xs bg-slate-900/80 p-2.5 rounded-xl border border-slate-800">
                          <span className="font-medium text-slate-200">{item.skillName}</span>
                          <span className="text-slate-400">Effort: {(item.effort * 100).toFixed(0)}%</span>
                        </div>
                      ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* TAB 4: LEARNING TIMELINE */}
        {activeTab === 'timeline' && (
          <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 space-y-6">
            <h3 className="text-lg font-bold text-slate-200">Career Readiness Timeline</h3>

            <div className="relative border-l-2 border-indigo-500/30 ml-4 space-y-8 pl-6">
              {report?.visualAnalytics?.learningTimeline?.map((step, idx) => (
                <div key={idx} className="relative">
                  <div className="absolute -left-[31px] top-0 w-6 h-6 rounded-full bg-indigo-600 border-4 border-slate-950 flex items-center justify-center text-[10px] font-bold text-white">
                    {step.stepNumber}
                  </div>
                  <div className="bg-slate-950/80 border border-slate-800 p-4 rounded-2xl space-y-2">
                    <span className="text-[10px] font-bold text-indigo-400 uppercase tracking-wider">{step.phase}</span>
                    <h4 className="text-sm font-bold text-white">{step.title}</h4>
                    <p className="text-xs text-slate-400">Est. Duration: ~{step.estimatedWeeks} Weeks</p>
                    <div className="flex flex-wrap gap-1.5 mt-2">
                      {step.skillsCovered?.map((s, i) => (
                        <span key={i} className="text-[10px] bg-indigo-500/10 text-indigo-300 border border-indigo-500/20 px-2 py-0.5 rounded-md">
                          {s}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* TAB 5: IMPROVEMENT RECOMMENDATIONS */}
        {activeTab === 'recommendations' && (
          <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-6 space-y-6">
            <h3 className="text-lg font-bold text-slate-200">Targeted Improvement Plan</h3>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {report?.improvementRecommendations?.map((rec, idx) => (
                <div key={idx} className="bg-slate-950/80 border border-slate-800 p-5 rounded-2xl flex flex-col justify-between space-y-4">
                  <div>
                    <div className="flex items-center justify-between">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-md uppercase tracking-wider ${
                        rec.recommendationType === 'PROJECT' ? 'bg-pink-500/10 text-pink-400' :
                        rec.recommendationType === 'CERTIFICATION' ? 'bg-purple-500/10 text-purple-400' :
                        rec.recommendationType === 'COURSE' ? 'bg-indigo-500/10 text-indigo-400' :
                        'bg-emerald-500/10 text-emerald-400'
                      }`}>
                        {rec.recommendationType}
                      </span>
                      <span className="text-xs text-slate-400">{rec.estimatedDuration}</span>
                    </div>
                    <h4 className="text-sm font-bold text-white mt-2">{rec.title}</h4>
                    <p className="text-xs text-slate-400 mt-1 leading-relaxed">{rec.description}</p>
                  </div>

                  {rec.url && (
                    <a
                      href={rec.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="inline-flex items-center text-xs font-semibold text-indigo-400 hover:text-indigo-300 space-x-1"
                    >
                      <span>Access Resource</span>
                      <ChevronRight className="w-3.5 h-3.5" />
                    </a>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
