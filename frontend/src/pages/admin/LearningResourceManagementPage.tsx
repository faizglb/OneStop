import React, { useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { BookOpen, Plus, Search, Trash2 } from 'lucide-react';

export const LearningResourceManagementPage: React.FC = () => {
  const [resources, setResources] = useState([
    { id: '1', title: 'Complete Python Bootcamp: Go Zero to Hero', provider: 'Udemy', type: 'COURSE', rating: 4.8, price: 'Paid' },
    { id: '2', title: 'Deep Learning with PyTorch', provider: 'Official PyTorch Docs', type: 'ARTICLE', rating: 4.9, price: 'Free' },
    { id: '3', title: 'System Design Interview – An insider\'s guide', provider: 'Alex Xu', type: 'BOOK', rating: 4.9, price: 'Paid' },
    { id: '4', title: 'LeetCode 75 Study Plan', provider: 'LeetCode', type: 'CODING_PLATFORM', rating: 4.7, price: 'Free' }
  ]);

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <BookOpen className="h-6 w-6 mr-2 text-indigo-600" /> Learning Resources & Course Index
            </h1>
            <p className="text-sm text-slate-500">Manage recommended courses, books, video tutorials, GitHub repos, and coding platforms.</p>
          </div>
          <button onClick={() => alert('Add Resource')} className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm font-medium shadow">
            <Plus className="h-4 w-4" />
            <span>Add Resource</span>
          </button>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">Resource Title</th>
                <th className="p-4">Provider</th>
                <th className="p-4">Type</th>
                <th className="p-4">Rating</th>
                <th className="p-4">Access</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {resources.map((r) => (
                <tr key={r.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                  <td className="p-4 font-semibold text-slate-900 dark:text-white">{r.title}</td>
                  <td className="p-4 text-xs">{r.provider}</td>
                  <td className="p-4"><span className="px-2 py-0.5 bg-indigo-50 text-indigo-700 dark:bg-indigo-900/40 text-xs font-semibold rounded">{r.type}</span></td>
                  <td className="p-4 text-xs font-bold text-amber-500">★ {r.rating}</td>
                  <td className="p-4 text-xs font-mono">{r.price}</td>
                  <td className="p-4 text-right">
                    <button onClick={() => setResources(resources.filter(item => item.id !== r.id))} className="p-1.5 text-rose-600 hover:bg-rose-50 rounded">
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
