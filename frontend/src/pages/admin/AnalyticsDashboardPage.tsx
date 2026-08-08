import React from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { BarChart3, TrendingUp, Users, Award, BookOpen, CheckSquare } from 'lucide-react';

export const AnalyticsDashboardPage: React.FC = () => {
  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
            <BarChart3 className="h-6 w-6 mr-2 text-indigo-600" /> Platform Growth & System Analytics
          </h1>
          <p className="text-sm text-slate-500">Comprehensive analytics across user growth, assessment trends, resume score distributions, and AI token consumption.</p>
        </div>

        {/* Analytics Highlights */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          <div className="bg-white dark:bg-slate-800 p-5 rounded-xl border border-slate-200 dark:border-slate-700 shadow-sm space-y-2">
            <div className="text-xs font-semibold text-slate-500 uppercase">Assessment Trends</div>
            <div className="text-3xl font-bold text-indigo-600">+34% vs last month</div>
            <p className="text-xs text-slate-400">RIASEC Holland personality and aptitude tests taken by students.</p>
          </div>

          <div className="bg-white dark:bg-slate-800 p-5 rounded-xl border border-slate-200 dark:border-slate-700 shadow-sm space-y-2">
            <div className="text-xs font-semibold text-slate-500 uppercase">Resume ATS Score Trends</div>
            <div className="text-3xl font-bold text-emerald-600">74.2 / 100 Avg</div>
            <p className="text-xs text-slate-400">Average ATS resume quality score after AI optimization feedback.</p>
          </div>

          <div className="bg-white dark:bg-slate-800 p-5 rounded-xl border border-slate-200 dark:border-slate-700 shadow-sm space-y-2">
            <div className="text-xs font-semibold text-slate-500 uppercase">Skill Gap Readiness</div>
            <div className="text-3xl font-bold text-purple-600">68.5% Average</div>
            <p className="text-xs text-slate-400">Student career readiness score before roadmap completion.</p>
          </div>
        </div>

        {/* Visual Trend Bars */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="bg-white dark:bg-slate-800 p-6 rounded-xl border border-slate-200 dark:border-slate-700 shadow-sm">
            <h3 className="font-semibold text-slate-800 dark:text-white mb-4">Monthly Active Student Growth (2026)</h3>
            <div className="h-48 flex items-end space-x-4 border-b border-slate-200 dark:border-slate-700 pb-2">
              {[
                { month: 'Jan', val: 40 },
                { month: 'Feb', val: 55 },
                { month: 'Mar', val: 70 },
                { month: 'Apr', val: 85 },
                { month: 'May', val: 92 },
                { month: 'Jun', val: 100 },
              ].map((item, idx) => (
                <div key={idx} className="flex-1 flex flex-col items-center">
                  <div className="w-full bg-indigo-500 rounded-t hover:bg-indigo-600 transition-all" style={{ height: `${item.val}%` }} />
                  <span className="text-xs text-slate-400 mt-2">{item.month}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="bg-white dark:bg-slate-800 p-6 rounded-xl border border-slate-200 dark:border-slate-700 shadow-sm">
            <h3 className="font-semibold text-slate-800 dark:text-white mb-4">AI Token Usage by Module</h3>
            <div className="space-y-4">
              <div>
                <div className="flex justify-between text-xs mb-1">
                  <span>AI Career Mentor Chatbot</span>
                  <span className="font-mono">45% (203k tokens)</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                  <div className="bg-indigo-600 h-full" style={{ width: '45%' }} />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-xs mb-1">
                  <span>AI Resume Analyzer</span>
                  <span className="font-mono">25% (112k tokens)</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                  <div className="bg-purple-500 h-full" style={{ width: '25%' }} />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-xs mb-1">
                  <span>AI Personalized Roadmap Generator</span>
                  <span className="font-mono">18% (81k tokens)</span>
                </div>
                <div className="w-full bg-slate-100 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                  <div className="bg-cyan-500 h-full" style={{ width: '18%' }} />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </AdminLayout>
  );
};
