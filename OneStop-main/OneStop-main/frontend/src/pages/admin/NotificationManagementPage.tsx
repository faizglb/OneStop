import React, { useEffect, useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { adminService, AdminNotification } from '@/services/adminService';
import { Bell, Plus, Trash2, Megaphone, AlertTriangle, Send } from 'lucide-react';

export const NotificationManagementPage: React.FC = () => {
  const [notifications, setNotifications] = useState<AdminNotification[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [newNotice, setNewNotice] = useState({
    title: '',
    message: '',
    type: 'ANNOUNCEMENT',
    targetRole: 'ALL',
    priority: 'NORMAL'
  });

  const loadNotices = async () => {
    setLoading(true);
    try {
      const data = await adminService.getNotifications();
      setNotifications(data.content || []);
    } catch (err) {
      console.error('Failed to load notifications:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotices();
  }, []);

  const handleCreate = async () => {
    if (!newNotice.title || !newNotice.message) return;
    try {
      await adminService.createNotification(newNotice);
      setShowModal(false);
      setNewNotice({ title: '', message: '', type: 'ANNOUNCEMENT', targetRole: 'ALL', priority: 'NORMAL' });
      loadNotices();
    } catch (err) {
      alert('Failed to broadcast notification');
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await adminService.deleteNotification(id);
      loadNotices();
    } catch (err) {
      alert('Failed to delete notification');
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <Bell className="h-6 w-6 mr-2 text-indigo-600" /> Platform Notifications & Announcements
            </h1>
            <p className="text-sm text-slate-500">Publish system maintenance alerts, scholarship deadline warnings, and broadcast updates.</p>
          </div>
          <button
            onClick={() => setShowModal(true)}
            className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-lg shadow transition-all self-start sm:self-auto text-sm"
          >
            <Plus className="h-4 w-4" />
            <span>Broadcast Message</span>
          </button>
        </div>

        {/* Notifications Table */}
        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">Title & Message</th>
                <th className="p-4">Type</th>
                <th className="p-4">Target Audience</th>
                <th className="p-4">Priority</th>
                <th className="p-4">Created</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {loading ? (
                <tr><td colSpan={6} className="p-8 text-center text-slate-400">Loading notifications...</td></tr>
              ) : notifications.length === 0 ? (
                <tr><td colSpan={6} className="p-8 text-center text-slate-400">No active broadcast notifications.</td></tr>
              ) : (
                notifications.map((n) => (
                  <tr key={n.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                    <td className="p-4">
                      <div className="font-semibold text-slate-900 dark:text-white flex items-center">
                        <Megaphone className="h-4 w-4 mr-2 text-indigo-500" />
                        {n.title}
                      </div>
                      <div className="text-xs text-slate-500 mt-1 max-w-md">{n.message}</div>
                    </td>
                    <td className="p-4"><span className="px-2 py-0.5 bg-indigo-50 dark:bg-indigo-900/40 text-indigo-600 dark:text-indigo-300 text-xs font-semibold rounded">{n.type}</span></td>
                    <td className="p-4 text-xs font-mono">{n.targetRole}</td>
                    <td className="p-4">
                      <span className={`px-2 py-0.5 text-xs font-semibold rounded ${
                        n.priority === 'HIGH' || n.priority === 'URGENT' ? 'bg-rose-100 text-rose-700' : 'bg-slate-100 text-slate-700'
                      }`}>
                        {n.priority}
                      </span>
                    </td>
                    <td className="p-4 text-xs text-slate-400">{new Date(n.createdAt).toLocaleDateString()}</td>
                    <td className="p-4 text-right">
                      <button onClick={() => handleDelete(n.id)} className="p-1.5 text-rose-600 hover:bg-rose-50 rounded">
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Broadcast Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white dark:bg-slate-800 rounded-xl p-6 max-w-md w-full shadow-2xl space-y-4">
            <h3 className="text-lg font-bold text-slate-900 dark:text-white flex items-center">
              <Send className="h-5 w-5 mr-2 text-indigo-600" /> Broadcast Platform Announcement
            </h3>
            <input
              type="text"
              placeholder="Announcement Title"
              value={newNotice.title}
              onChange={(e) => setNewNotice({ ...newNotice, title: e.target.value })}
              className="w-full p-2.5 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm"
            />
            <textarea
              placeholder="Message body content..."
              value={newNotice.message}
              onChange={(e) => setNewNotice({ ...newNotice, message: e.target.value })}
              className="w-full p-2.5 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm"
              rows={3}
            />
            <div className="grid grid-cols-2 gap-3">
              <select
                value={newNotice.type}
                onChange={(e) => setNewNotice({ ...newNotice, type: e.target.value })}
                className="p-2 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-xs"
              >
                <option value="ANNOUNCEMENT">ANNOUNCEMENT</option>
                <option value="MAINTENANCE">MAINTENANCE</option>
                <option value="DEADLINE">DEADLINE</option>
                <option value="BROADCAST">BROADCAST</option>
              </select>

              <select
                value={newNotice.priority}
                onChange={(e) => setNewNotice({ ...newNotice, priority: e.target.value })}
                className="p-2 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-xs"
              >
                <option value="NORMAL">NORMAL Priority</option>
                <option value="HIGH">HIGH Priority</option>
                <option value="URGENT">URGENT Priority</option>
              </select>
            </div>

            <div className="flex justify-end space-x-3">
              <button onClick={() => setShowModal(false)} className="px-4 py-2 text-sm text-slate-600">Cancel</button>
              <button onClick={handleCreate} className="px-4 py-2 text-sm bg-indigo-600 text-white font-medium rounded-lg">Publish Notice</button>
            </div>
          </div>
        </div>
      )}
    </AdminLayout>
  );
};
