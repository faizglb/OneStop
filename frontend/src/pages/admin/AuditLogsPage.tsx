import React, { useEffect, useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { adminService, AuditLog } from '@/services/adminService';
import { FileText, Search, Shield, Clock } from 'lucide-react';

export const AuditLogsPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [query, setQuery] = useState('');

  const loadLogs = async () => {
    setLoading(true);
    try {
      const data = await adminService.getAuditLogs(0, 20, query);
      setLogs(data.content || []);
    } catch (err) {
      console.error('Failed to load audit logs:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, [query]);

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
            <FileText className="h-6 w-6 mr-2 text-indigo-600" /> Platform Security & Admin Audit Trail
          </h1>
          <p className="text-sm text-slate-500">Immutable audit log recording all administrator actions, role modifications, AI updates, and data imports.</p>
        </div>

        {/* Search */}
        <div className="bg-white dark:bg-slate-800 p-4 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700">
          <div className="relative">
            <Search className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search by action, module, or admin email..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="w-full pl-9 pr-4 py-2 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-lg text-sm focus:outline-none"
            />
          </div>
        </div>

        {/* Audit Logs Table */}
        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">Timestamp</th>
                <th className="p-4">Actor Admin</th>
                <th className="p-4">Action</th>
                <th className="p-4">Module</th>
                <th className="p-4">Details</th>
                <th className="p-4">IP Address</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {loading ? (
                <tr><td colSpan={6} className="p-8 text-center text-slate-400">Loading audit logs...</td></tr>
              ) : logs.length === 0 ? (
                <tr><td colSpan={6} className="p-8 text-center text-slate-400">No audit logs found.</td></tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                    <td className="p-4 text-xs text-slate-400 font-mono flex items-center">
                      <Clock className="h-3 w-3 mr-1 text-slate-400" />
                      {new Date(log.createdAt).toLocaleString()}
                    </td>
                    <td className="p-4">
                      <div className="font-semibold text-slate-900 dark:text-white">{log.actorEmail}</div>
                      <span className="text-[10px] font-mono px-1.5 py-0.5 bg-indigo-50 dark:bg-indigo-900/50 text-indigo-600 dark:text-indigo-300 rounded">
                        {log.actorRole}
                      </span>
                    </td>
                    <td className="p-4 font-mono text-xs font-bold text-indigo-600 dark:text-indigo-400">{log.action}</td>
                    <td className="p-4 text-xs font-semibold">{log.module}</td>
                    <td className="p-4 text-xs text-slate-600 dark:text-slate-300 max-w-xs truncate">{log.details}</td>
                    <td className="p-4 text-xs font-mono text-slate-400">{log.ipAddress}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </AdminLayout>
  );
};
