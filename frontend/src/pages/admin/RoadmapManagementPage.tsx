import React, { useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { GitFork, Plus, Trash2 } from 'lucide-react';

export const RoadmapManagementPage: React.FC = () => {
  const [templates, setTemplates] = useState([
    { id: '1', name: 'Frontend Developer Mastery Roadmap', duration: '6 Months', milestones: 8, totalTasks: 42 },
    { id: '2', name: 'AI & Data Science Professional Path', duration: '9 Months', milestones: 12, totalTasks: 65 },
    { id: '3', name: 'Full Stack Java Spring Boot Specialist', duration: '6 Months', milestones: 7, totalTasks: 38 }
  ]);

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <GitFork className="h-6 w-6 mr-2 text-indigo-600" /> Career Roadmap Templates & Milestone Trees
            </h1>
            <p className="text-sm text-slate-500">Configure default career paths, milestone phases, and step-by-step task trees.</p>
          </div>
          <button onClick={() => alert('Add Roadmap Template')} className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm font-medium shadow">
            <Plus className="h-4 w-4" />
            <span>New Template</span>
          </button>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">Roadmap Template</th>
                <th className="p-4">Estimated Duration</th>
                <th className="p-4">Milestones</th>
                <th className="p-4">Tasks</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {templates.map((t) => (
                <tr key={t.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                  <td className="p-4 font-semibold text-slate-900 dark:text-white">{t.name}</td>
                  <td className="p-4 text-xs font-mono">{t.duration}</td>
                  <td className="p-4 text-xs font-semibold text-indigo-600">{t.milestones} Phases</td>
                  <td className="p-4 text-xs font-mono">{t.totalTasks} Tasks</td>
                  <td className="p-4 text-right">
                    <button onClick={() => setTemplates(templates.filter(item => item.id !== t.id))} className="p-1.5 text-rose-600 hover:bg-rose-50 rounded">
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </AdminLayout>
  );
};
