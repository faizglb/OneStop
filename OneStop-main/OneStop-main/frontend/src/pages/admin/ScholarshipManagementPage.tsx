import React, { useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { Award, Plus, Upload, Search, Trash2 } from 'lucide-react';

export const ScholarshipManagementPage: React.FC = () => {
  const [scholarships, setScholarships] = useState([
    { id: '1', name: 'KVPY National Fellowship', provider: 'DST, Govt of India', amount: '₹80,000 / year', deadline: '2026-10-31', eligibility: 'Science/Engineering, 60% Marks' },
    { id: '2', name: 'INSPIRE Scholarship SHE', provider: 'Department of Science and Tech', amount: '₹80,000 / year', deadline: '2026-11-15', eligibility: 'Top 1% Class 12th Board' },
    { id: '3', name: 'PM Vidyalaxmi Scheme', provider: 'Central Govt', amount: 'Full Fee Waiver', deadline: '2026-12-01', eligibility: 'Income < ₹8 LPA, NIRF Top 100' },
  ]);

  const [search, setSearch] = useState('');

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <Award className="h-6 w-6 mr-2 text-indigo-600" /> Scholarship Scheme Management
            </h1>
            <p className="text-sm text-slate-500">Configure national & private scholarship programs, eligibility rules, and deadlines.</p>
          </div>
          <div className="flex space-x-3">
            <button onClick={() => alert('Bulk Import')} className="flex items-center space-x-2 px-3 py-2 bg-slate-800 text-white rounded-lg text-sm font-medium">
              <Upload className="h-4 w-4" />
              <span>Bulk Import</span>
            </button>
            <button onClick={() => alert('Add Scholarship')} className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 text-white rounded-lg text-sm font-medium shadow">
              <Plus className="h-4 w-4" />
              <span>New Program</span>
            </button>
          </div>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">Program Name</th>
                <th className="p-4">Provider</th>
                <th className="p-4">Grant Amount</th>
                <th className="p-4">Application Deadline</th>
                <th className="p-4">Eligibility Overview</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {scholarships.map((s) => (
                <tr key={s.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                  <td className="p-4 font-semibold text-slate-900 dark:text-white">{s.name}</td>
                  <td className="p-4 text-xs">{s.provider}</td>
                  <td className="p-4 text-xs font-mono font-bold text-amber-600">{s.amount}</td>
                  <td className="p-4 text-xs text-rose-500 font-medium">{s.deadline}</td>
                  <td className="p-4 text-xs text-slate-600 dark:text-slate-400">{s.eligibility}</td>
                  <td className="p-4 text-right">
                    <button onClick={() => setScholarships(scholarships.filter(item => item.id !== s.id))} className="p-1.5 text-rose-600 hover:bg-rose-50 rounded">
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
