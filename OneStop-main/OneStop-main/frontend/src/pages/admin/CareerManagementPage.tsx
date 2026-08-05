import React, { useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { Briefcase, Plus, Upload, Download, Edit3, Trash2, Search, CheckCircle2 } from 'lucide-react';

export const CareerManagementPage: React.FC = () => {
  const [careers, setCareers] = useState([
    { id: '1', title: 'AI & Machine Learning Engineer', category: 'Technology', avgSalary: '₹14-35 LPA', skills: 'Python, PyTorch, Scikit-learn, MLOps', demand: 'VERY_HIGH' },
    { id: '2', title: 'Full Stack Web Developer', category: 'Software Development', avgSalary: '₹8-22 LPA', skills: 'React, Node.js, Java Spring Boot, SQL', demand: 'HIGH' },
    { id: '3', title: 'Cybersecurity Specialist', category: 'Information Security', avgSalary: '₹10-28 LPA', skills: 'Network Security, Ethical Hacking, SIEM', demand: 'HIGH' },
    { id: '4', title: 'Data Scientist', category: 'Data & Analytics', avgSalary: '₹12-30 LPA', skills: 'Python, SQL, R, Tableau, Statistics', demand: 'VERY_HIGH' },
    { id: '5', title: 'Cloud Solutions Architect', category: 'Cloud Computing', avgSalary: '₹18-45 LPA', skills: 'AWS, Azure, Docker, Kubernetes', demand: 'HIGH' },
  ]);

  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [newCareer, setNewCareer] = useState({ title: '', category: 'Technology', avgSalary: '', skills: '', demand: 'HIGH' });

  const filteredCareers = careers.filter(c =>
    c.title.toLowerCase().includes(search.toLowerCase()) ||
    c.category.toLowerCase().includes(search.toLowerCase())
  );

  const handleAddCareer = () => {
    if (!newCareer.title) return;
    setCareers([...careers, { id: String(Date.now()), ...newCareer }]);
    setShowModal(false);
    setNewCareer({ title: '', category: 'Technology', avgSalary: '', skills: '', demand: 'HIGH' });
  };

  const handleDelete = (id: string) => {
    setCareers(careers.filter(c => c.id !== id));
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <Briefcase className="h-6 w-6 mr-2 text-indigo-600" /> Career Catalog Management
            </h1>
            <p className="text-sm text-slate-500">Manage career categories, skills, salary data, future scope, and industry trends.</p>
          </div>
          <div className="flex space-x-3">
            <button
              onClick={() => alert('Bulk JSON/CSV dataset import initiated')}
              className="flex items-center space-x-2 px-3 py-2 bg-slate-800 hover:bg-slate-900 text-white rounded-lg text-sm font-medium"
            >
              <Upload className="h-4 w-4" />
              <span>Bulk Import</span>
            </button>
            <button
              onClick={() => setShowModal(true)}
              className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-medium shadow"
            >
              <Plus className="h-4 w-4" />
              <span>Add Career Role</span>
            </button>
          </div>
        </div>

        {/* Search */}
        <div className="bg-white dark:bg-slate-800 p-4 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700">
          <div className="relative">
            <Search className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search careers or skills..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-lg text-sm focus:outline-none"
            />
          </div>
        </div>

        {/* Catalog Table */}
        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">Career Role</th>
                <th className="p-4">Category</th>
                <th className="p-4">Salary Range</th>
                <th className="p-4">Key Skills Required</th>
                <th className="p-4">Demand</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {filteredCareers.map((c) => (
                <tr key={c.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                  <td className="p-4 font-semibold text-slate-900 dark:text-white">{c.title}</td>
                  <td className="p-4"><span className="px-2 py-1 bg-indigo-50 dark:bg-indigo-900/40 text-indigo-600 dark:text-indigo-300 text-xs font-semibold rounded">{c.category}</span></td>
                  <td className="p-4 text-xs font-mono font-medium text-emerald-600 dark:text-emerald-400">{c.avgSalary}</td>
                  <td className="p-4 text-xs text-slate-600 dark:text-slate-400">{c.skills}</td>
                  <td className="p-4">
                    <span className="px-2 py-0.5 text-xs font-semibold rounded bg-emerald-100 text-emerald-700 dark:bg-emerald-900/40 dark:text-emerald-300">
                      {c.demand}
                    </span>
                  </td>
                  <td className="p-4 text-right space-x-2">
                    <button onClick={() => handleDelete(c.id)} className="p-1.5 text-rose-600 hover:bg-rose-50 rounded">
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Career Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-800 rounded-xl p-6 max-w-md w-full shadow-2xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">Add New Career Role</h3>
            <input
              type="text"
              placeholder="Career Title (e.g. DevOps Engineer)"
              value={newCareer.title}
              onChange={(e) => setNewCareer({ ...newCareer, title: e.target.value })}
              className="w-full p-2.5 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm"
            />
            <input
              type="text"
              placeholder="Average Salary (e.g. ₹10-25 LPA)"
              value={newCareer.avgSalary}
              onChange={(e) => setNewCareer({ ...newCareer, avgSalary: e.target.value })}
              className="w-full p-2.5 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm"
            />
            <input
              type="text"
              placeholder="Key Skills (comma separated)"
              value={newCareer.skills}
              onChange={(e) => setNewCareer({ ...newCareer, skills: e.target.value })}
              className="w-full p-2.5 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm"
            />
            <div className="flex justify-end space-x-3">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm text-slate-600">Cancel</button>
              <button onClick={handleAddCareer} className="px-4 py-2 text-sm bg-indigo-600 text-white font-medium rounded-lg">Save Career</button>
            </div>
          </div>
        </div>
      )}
    </AdminLayout>
  );
};
