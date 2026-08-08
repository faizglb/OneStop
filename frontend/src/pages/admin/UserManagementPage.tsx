import React, { useEffect, useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { adminService, AdminUser } from '@/services/adminService';
import {
  Users,
  Search,
  UserCheck,
  UserX,
  KeyRound,
  Shield,
  Download,
  MoreVertical,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';

export const UserManagementPage: React.FC = () => {
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [selectedUser, setSelectedUser] = useState<AdminUser | null>(null);
  const [suspendReason, setSuspendReason] = useState('');
  const [showSuspendModal, setShowSuspendModal] = useState(false);
  const [showRoleModal, setShowRoleModal] = useState(false);
  const [newRole, setNewRole] = useState('STUDENT');
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [newPassword, setNewPassword] = useState('');

  const loadUsers = async () => {
    setLoading(true);
    try {
      const data = await adminService.getUsers(0, 50, search, roleFilter);
      setUsers(data.content || []);
    } catch (err) {
      console.error('Failed to fetch users:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, [search, roleFilter]);

  const handleSuspend = async () => {
    if (!selectedUser) return;
    try {
      await adminService.suspendUser(selectedUser.id, suspendReason);
      setShowSuspendModal(false);
      loadUsers();
    } catch (err) {
      alert('Failed to suspend user');
    }
  };

  const handleActivate = async (user: AdminUser) => {
    try {
      await adminService.activateUser(user.id);
      loadUsers();
    } catch (err) {
      alert('Failed to activate user');
    }
  };

  const handleAssignRole = async () => {
    if (!selectedUser) return;
    try {
      await adminService.assignRole(selectedUser.id, newRole);
      setShowRoleModal(false);
      loadUsers();
    } catch (err) {
      alert('Failed to assign role');
    }
  };

  const handleResetPassword = async () => {
    if (!selectedUser || !newPassword) return;
    try {
      await adminService.resetPassword(selectedUser.id, newPassword);
      setShowPasswordModal(false);
      setNewPassword('');
      alert('Password reset successfully');
    } catch (err) {
      alert('Failed to reset password');
    }
  };

  const handleExportCSV = async () => {
    try {
      const data = await adminService.exportUsers();
      const csvContent = 'data:text/csv;charset=utf-8,' +
        ['ID,Email,First Name,Last Name,Role,Tier,Status,Created At']
          .concat(data.map(u => `${u.id},${u.email},${u.firstName},${u.lastName},${u.role},${u.accountTier},${u.suspended ? 'SUSPENDED' : 'ACTIVE'},${u.createdAt}`))
          .join('\n');
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement('a');
      link.setAttribute('href', encodedUri);
      link.setAttribute('download', `acea_users_export_${Date.now()}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (err) {
      alert('Export failed');
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <Users className="h-6 w-6 mr-2 text-indigo-600" /> User Directory & Account Management
            </h1>
            <p className="text-sm text-slate-500">View, suspend, assign roles, reset credentials, and audit user status.</p>
          </div>
          <button
            onClick={handleExportCSV}
            className="flex items-center space-x-2 px-4 py-2 bg-slate-800 hover:bg-slate-900 text-white rounded-lg shadow text-sm font-medium transition-all"
          >
            <Download className="h-4 w-4" />
            <span>Export CSV</span>
          </button>
        </div>

        {/* Filter Controls */}
        <div className="bg-white dark:bg-slate-800 p-4 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 flex flex-col md:flex-row gap-4 justify-between">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search by name or email..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <div className="flex items-center space-x-3">
            <select
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              className="px-3 py-2 bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-lg text-sm focus:outline-none"
            >
              <option value="">All Roles</option>
              <option value="SUPER_ADMIN">Super Admin</option>
              <option value="ADMIN">Admin</option>
              <option value="CONTENT_MANAGER">Content Manager</option>
              <option value="STUDENT">Student</option>
              <option value="PREMIUM_STUDENT">Premium Student</option>
            </select>
          </div>
        </div>

        {/* Data Table */}
        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
                <tr>
                  <th className="p-4">User</th>
                  <th className="p-4">Role</th>
                  <th className="p-4">Tier</th>
                  <th className="p-4">Status</th>
                  <th className="p-4">Logins</th>
                  <th className="p-4">Registered</th>
                  <th className="p-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
                {loading ? (
                  <tr>
                    <td colSpan={7} className="p-8 text-center text-slate-400">Loading user records...</td>
                  </tr>
                ) : users.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="p-8 text-center text-slate-400">No users found matching query.</td>
                  </tr>
                ) : (
                  users.map((u) => (
                    <tr key={u.id} className="hover:bg-slate-50 dark:hover:bg-slate-750 transition-colors">
                      <td className="p-4">
                        <div className="font-semibold text-slate-900 dark:text-white">{u.firstName} {u.lastName}</div>
                        <div className="text-xs text-slate-400">{u.email}</div>
                      </td>
                      <td className="p-4">
                        <span className={`px-2.5 py-1 rounded-full text-xs font-semibold ${
                          u.role === 'SUPER_ADMIN' ? 'bg-purple-100 text-purple-700 dark:bg-purple-900/50 dark:text-purple-300' :
                          u.role === 'ADMIN' ? 'bg-indigo-100 text-indigo-700 dark:bg-indigo-900/50 dark:text-indigo-300' :
                          u.role === 'CONTENT_MANAGER' ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/50 dark:text-amber-300' :
                          'bg-slate-100 text-slate-700 dark:bg-slate-700 dark:text-slate-300'
                        }`}>
                          {u.role}
                        </span>
                      </td>
                      <td className="p-4">
                        <span className="text-xs font-mono px-2 py-0.5 rounded bg-slate-100 dark:bg-slate-700">
                          {u.accountTier}
                        </span>
                      </td>
                      <td className="p-4">
                        {u.suspended ? (
                          <span className="inline-flex items-center text-xs font-semibold text-rose-600 dark:text-rose-400 bg-rose-50 dark:bg-rose-900/30 px-2.5 py-1 rounded-full">
                            <AlertCircle className="h-3 w-3 mr-1" /> Suspended
                          </span>
                        ) : (
                          <span className="inline-flex items-center text-xs font-semibold text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-900/30 px-2.5 py-1 rounded-full">
                            <CheckCircle2 className="h-3 w-3 mr-1" /> Active
                          </span>
                        )}
                      </td>
                      <td className="p-4 text-xs font-mono">{u.loginCount}</td>
                      <td className="p-4 text-xs text-slate-400">
                        {new Date(u.createdAt).toLocaleDateString()}
                      </td>
                      <td className="p-4 text-right space-x-2">
                        <button
                          onClick={() => {
                            setSelectedUser(u);
                            setNewRole(u.role);
                            setShowRoleModal(true);
                          }}
                          className="p-1.5 text-indigo-600 hover:bg-indigo-50 rounded"
                          title="Change Role"
                        >
                          <Shield className="h-4 w-4" />
                        </button>

                        <button
                          onClick={() => {
                            setSelectedUser(u);
                            setShowPasswordModal(true);
                          }}
                          className="p-1.5 text-amber-600 hover:bg-amber-50 rounded"
                          title="Reset Password"
                        >
                          <KeyRound className="h-4 w-4" />
                        </button>

                        {u.suspended ? (
                          <button
                            onClick={() => handleActivate(u)}
                            className="p-1.5 text-emerald-600 hover:bg-emerald-50 rounded"
                            title="Activate Account"
                          >
                            <UserCheck className="h-4 w-4" />
                          </button>
                        ) : (
                          <button
                            onClick={() => {
                              setSelectedUser(u);
                              setShowSuspendModal(true);
                            }}
                            className="p-1.5 text-rose-600 hover:bg-rose-50 rounded"
                            title="Suspend Account"
                          >
                            <UserX className="h-4 w-4" />
                          </button>
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Suspend Modal */}
      {showSuspendModal && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-800 rounded-xl p-6 max-w-md w-full shadow-2xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">Suspend User Account</h3>
            <p className="text-sm text-slate-500">Provide a reason for suspending <span className="font-semibold">{selectedUser?.email}</span>.</p>
            <textarea
              value={suspendReason}
              onChange={(e) => setSuspendReason(e.target.value)}
              placeholder="Reason for suspension..."
              className="w-full p-3 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm focus:outline-none"
              rows={3}
            />
            <div className="flex justify-end space-x-3">
              <button onClick={() => setShowSuspendModal(false)} className="px-4 py-2 text-sm text-slate-600">Cancel</button>
              <button onClick={handleSuspend} className="px-4 py-2 text-sm bg-rose-600 text-white font-medium rounded-lg">Confirm Suspension</button>
            </div>
          </div>
        </div>
      )}

      {/* Assign Role Modal */}
      {showRoleModal && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-800 rounded-xl p-6 max-w-md w-full shadow-2xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">Assign Platform Role</h3>
            <p className="text-sm text-slate-500">Select new role for <span className="font-semibold">{selectedUser?.email}</span>.</p>
            <select
              value={newRole}
              onChange={(e) => setNewRole(e.target.value)}
              className="w-full p-3 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm"
            >
              <option value="STUDENT">STUDENT</option>
              <option value="PREMIUM_STUDENT">PREMIUM_STUDENT</option>
              <option value="CONTENT_MANAGER">CONTENT_MANAGER</option>
              <option value="ADMIN">ADMIN</option>
              <option value="SUPER_ADMIN">SUPER_ADMIN</option>
            </select>
            <div className="flex justify-end space-x-3">
              <button onClick={() => setShowRoleModal(false)} className="px-4 py-2 text-sm text-slate-600">Cancel</button>
              <button onClick={handleAssignRole} className="px-4 py-2 text-sm bg-indigo-600 text-white font-medium rounded-lg">Save Role</button>
            </div>
          </div>
        </div>
      )}

      {/* Password Reset Modal */}
      {showPasswordModal && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-800 rounded-xl p-6 max-w-md w-full shadow-2xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white">Reset User Password</h3>
            <p className="text-sm text-slate-500">Enter temporary password for <span className="font-semibold">{selectedUser?.email}</span>.</p>
            <input
              type="password"
              placeholder="New password (min 6 chars)"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className="w-full p-3 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm"
            />
            <div className="flex justify-end space-x-3">
              <button onClick={() => setShowPasswordModal(false)} className="px-4 py-2 text-sm text-slate-600">Cancel</button>
              <button onClick={handleResetPassword} className="px-4 py-2 text-sm bg-amber-600 text-white font-medium rounded-lg">Reset Password</button>
            </div>
          </div>
        </div>
      )}
    </AdminLayout>
  );
};
