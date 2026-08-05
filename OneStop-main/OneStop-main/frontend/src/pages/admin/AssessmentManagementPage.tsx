import React, { useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { CheckSquare, Plus, Upload, Search, Trash2 } from 'lucide-react';

export const AssessmentManagementPage: React.FC = () => {
  const [questions, setQuestions] = useState([
    { id: '1', category: 'APTITUDE', prompt: 'If 6 workers build a wall in 12 days, how long will 4 workers take?', difficulty: 'MEDIUM', timer: '60s', score: 10 },
    { id: '2', category: 'PERSONALITY', prompt: 'Do you enjoy solving open-ended logical puzzles independently?', difficulty: 'EASY', timer: '45s', score: 5 },
    { id: '3', category: 'TECHNICAL_DSA', prompt: 'What is the time complexity of searching in a balanced Binary Search Tree?', difficulty: 'HARD', timer: '90s', score: 15 },
  ]);

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <CheckSquare className="h-6 w-6 mr-2 text-indigo-600" /> Assessment Question Bank & Scoring Engine
            </h1>
            <p className="text-sm text-slate-500">Configure RIASEC Holland code tests, aptitude questions, timers, and difficulty parameters.</p>
          </div>
          <div className="flex space-x-3">
            <button onClick={() => alert('Bulk Question Import')} className="flex items-center space-x-2 px-3 py-2 bg-slate-800 text-white rounded-lg text-sm font-medium">
              <Upload className="h-4 w-4" />
              <span>Import Questions</span>
            </button>
            <button onClick={() => alert('Add Question')} className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm font-medium shadow">
              <Plus className="h-4 w-4" />
              <span>New Question</span>
            </button>
          </div>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">Category</th>
                <th className="p-4">Question Prompt</th>
                <th className="p-4">Difficulty</th>
                <th className="p-4">Time Limit</th>
                <th className="p-4">Points</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {questions.map((q) => (
                <tr key={q.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                  <td className="p-4 font-semibold text-indigo-600 dark:text-indigo-400 text-xs">{q.category}</td>
                  <td className="p-4 text-slate-800 dark:text-slate-200">{q.prompt}</td>
                  <td className="p-4">
                    <span className={`px-2 py-0.5 text-xs font-semibold rounded ${
                      q.difficulty === 'HARD' ? 'bg-rose-100 text-rose-700' :
                      q.difficulty === 'MEDIUM' ? 'bg-amber-100 text-amber-700' : 'bg-emerald-100 text-emerald-700'
                    }`}>
                      {q.difficulty}
                    </span>
                  </td>
                  <td className="p-4 text-xs font-mono">{q.timer}</td>
                  <td className="p-4 text-xs font-bold">{q.score} pts</td>
                  <td className="p-4 text-right">
                    <button onClick={() => setQuestions(questions.filter(item => item.id !== q.id))} className="p-1.5 text-rose-600 hover:bg-rose-50 rounded">
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
