import React, { useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { GraduationCap, Plus, Upload, Search, Trash2 } from 'lucide-react';

export const CollegeManagementPage: React.FC = () => {
  const [colleges, setColleges] = useState([
    { id: '1', name: 'IIT Bombay', state: 'Maharashtra', nirfRank: 1, courses: 'CSE, ECE, Mechanical', avgFee: '₹2.2L/yr', placementPct: '98%' },
    { id: '2', name: 'IISc Bangalore', state: 'Karnataka', nirfRank: 2, courses: 'BS Research, M.Tech', avgFee: '₹1.5L/yr', placementPct: '99%' },
    { id: '3', name: 'IIT Delhi', state: 'Delhi', nirfRank: 3, courses: 'CSE, Electrical, AI', avgFee: '₹2.3L/yr', placementPct: '97%' },
    { id: '4', name: 'BITS Pilani', state: 'Rajasthan', nirfRank: 15, courses: 'B.E. CS, EEE, Chemical', avgFee: '₹5.5L/yr', placementPct: '95%' },
  ]);

  const [search, setSearch] = useState('');

  const filteredColleges = colleges.filter(c =>
    c.name.toLowerCase().includes(search.toLowerCase()) ||
    c.state.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <GraduationCap className="h-6 w-6 mr-2 text-indigo-600" /> College Directory & NIRF Cutoffs
            </h1>
            <p className="text-sm text-slate-500">Manage institutes, courses, fee structures, cutoffs, placements, and hostel facilities.</p>
          </div>
          <div className="flex space-x-3">
            <button
              onClick={() => alert('Bulk CSV/Excel college data import triggered')}
              className="flex items-center space-x-2 px-3 py-2 bg-slate-800 hover:bg-slate-900 text-white rounded-lg text-sm font-medium"
            >
              <Upload className="h-4 w-4" />
              <span>CSV/Excel Import</span>
            </button>
            <button
              onClick={() => alert('Add College Modal')}
              className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-medium shadow"
            >
              <Plus className="h-4 w-4" />
              <span>Add College</span>
            </button>
          </div>
        </div>

        <div className="bg-white dark:bg-slate-800 p-4 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700">
          <div className="relative">
            <Search className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search college by name or state..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-lg text-sm focus:outline-none"
            />
          </div>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">College Name</th>
                <th className="p-4">State</th>
                <th className="p-4">NIRF Rank</th>
                <th className="p-4">Courses Offered</th>
                <th className="p-4">Annual Fee</th>
                <th className="p-4">Placement Rate</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {filteredColleges.map((c) => (
                <tr key={c.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                  <td className="p-4 font-semibold text-slate-900 dark:text-white">{c.name}</td>
                  <td className="p-4 text-xs">{c.state}</td>
                  <td className="p-4 font-mono font-bold text-indigo-600 dark:text-indigo-400">#{c.nirfRank}</td>
                  <td className="p-4 text-xs text-slate-600 dark:text-slate-400">{c.courses}</td>
                  <td className="p-4 text-xs font-mono">{c.avgFee}</td>
                  <td className="p-4 text-xs font-semibold text-emerald-600">{c.placementPct}</td>
                  <td className="p-4 text-right space-x-2">
                    <button onClick={() => setColleges(colleges.filter(col => col.id !== c.id))} className="p-1.5 text-rose-600 hover:bg-rose-50 rounded">
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
