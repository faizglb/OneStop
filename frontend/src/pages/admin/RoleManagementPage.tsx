import React, { useEffect, useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { adminService, RolePermission } from '@/services/adminService';
import { ShieldCheck, Check, X, ShieldAlert } from 'lucide-react';

export const RoleManagementPage: React.FC = () => {
  const [roles, setRoles] = useState<RolePermission[]>([]);
  const [loading, setLoading] = useState(true);

  const permissionsList = [
    { key: 'USERS_MANAGE', label: 'User Directory & Account Controls' },
    { key: 'ROLES_MANAGE', label: 'RBAC & Permission Grants' },
    { key: 'CONTENT_MANAGE', label: 'Careers, Colleges & Scholarships CRUD' },
    { key: 'AI_CONFIG_MANAGE', label: 'AI LLM Models & Hyperparameters' },
    { key: 'KNOWLEDGE_BASE_MANAGE', label: 'Vector Store Upload & Re-indexing' },
    { key: 'ANALYTICS_VIEW', label: 'Platform Analytics & Growth Telemetry' },
    { key: 'SYSTEM_HEALTH', label: 'System Health & Resource Metrics' }
  ];

  const loadRoles = async () => {
    setLoading(true);
    try {
      const data = await adminService.getRoles();
      setRoles(data);
    } catch (err) {
      console.error('Failed to load role permissions:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRoles();
  }, []);

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
            <ShieldAlert className="h-6 w-6 mr-2 text-indigo-600" /> Role-Based Access Control (RBAC) Matrix
          </h1>
          <p className="text-sm text-slate-500">Configure permission boundaries for SUPER_ADMIN, ADMIN, and CONTENT_MANAGER roles.</p>
        </div>

        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
                <tr>
                  <th className="p-4">Permission Capability</th>
                  <th className="p-4 text-center">SUPER_ADMIN</th>
                  <th className="p-4 text-center">ADMIN</th>
                  <th className="p-4 text-center">CONTENT_MANAGER</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
                {permissionsList.map((perm) => {
                  const superAdminHas = true;
                  const adminHas = perm.key !== 'ROLES_MANAGE' && perm.key !== 'AI_CONFIG_MANAGE';
                  const contentMgrHas = perm.key === 'CONTENT_MANAGE' || perm.key === 'ANALYTICS_VIEW';

                  return (
                    <tr key={perm.key} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                      <td className="p-4">
                        <div className="font-semibold text-slate-900 dark:text-white">{perm.label}</div>
                        <div className="text-xs text-slate-400 font-mono">{perm.key}</div>
                      </td>
                      <td className="p-4 text-center">
                        <span className="inline-flex p-1.5 bg-emerald-100 dark:bg-emerald-900/40 text-emerald-600 dark:text-emerald-400 rounded-full">
                          <Check className="h-4 w-4" />
                        </span>
                      </td>
                      <td className="p-4 text-center">
                        {adminHas ? (
                          <span className="inline-flex p-1.5 bg-emerald-100 dark:bg-emerald-900/40 text-emerald-600 dark:text-emerald-400 rounded-full">
                            <Check className="h-4 w-4" />
                          </span>
                        ) : (
                          <span className="inline-flex p-1.5 bg-rose-100 dark:bg-rose-900/40 text-rose-600 dark:text-rose-400 rounded-full">
                            <X className="h-4 w-4" />
                          </span>
                        )}
                      </td>
                      <td className="p-4 text-center">
                        {contentMgrHas ? (
                          <span className="inline-flex p-1.5 bg-emerald-100 dark:bg-emerald-900/40 text-emerald-600 dark:text-emerald-400 rounded-full">
                            <Check className="h-4 w-4" />
                          </span>
                        ) : (
                          <span className="inline-flex p-1.5 bg-rose-100 dark:bg-rose-900/40 text-rose-600 dark:text-rose-400 rounded-full">
                            <X className="h-4 w-4" />
                          </span>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </AdminLayout>
  );
};
