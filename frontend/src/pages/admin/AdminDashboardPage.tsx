import React, { useEffect, useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { adminService, AdminDashboardStats } from '@/services/adminService';
import {
  Users,
  CheckCircle,
  Briefcase,
  GraduationCap,
  Award,
  Cpu,
  HardDrive,
  Activity,
  Zap,
  TrendingUp,
  RefreshCw,
  Clock
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const [stats, setStats] = useState<AdminDashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchStats = async () => {
    setLoading(true);
    try {
      const data = await adminService.getDashboardStats();
      setStats(data);
    } catch (err) {
      console.error('Failed to load admin stats:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStats();
  }, []);

  return (
    <AdminLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white">Platform Control Dashboard</h1>
            <p className="text-sm text-slate-500 dark:text-slate-400">Real-time overview of users, AI operations, and ecosystem metrics.</p>
          </div>
          <button
            onClick={fetchStats}
            className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-lg shadow transition-all self-start sm:self-auto"
          >
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
            <span>Refresh Metrics</span>
          </button>
        </div>

        {/* Top Metric Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="bg-white dark:bg-slate-800 rounded-xl p-5 shadow-sm border border-slate-200 dark:border-slate-700 flex items-center space-x-4">
            <div className="p-3.5 bg-indigo-50 dark:bg-indigo-900/40 rounded-xl text-indigo-600 dark:text-indigo-400">
              <Users className="h-6 w-6" />
            </div>
            <div>
              <div className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase">Total Users</div>
              <div className="text-2xl font-bold text-slate-900 dark:text-white">{loading ? '...' : stats?.totalUsers}</div>
              <div className="text-xs text-emerald-500 font-medium flex items-center mt-0.5">
                <TrendingUp className="h-3 w-3 mr-1" /> DAU: {stats?.dailyActiveUsers} | MAU: {stats?.monthlyActiveUsers}
              </div>
            </div>
          </div>

          <div className="bg-white dark:bg-slate-800 rounded-xl p-5 shadow-sm border border-slate-200 dark:border-slate-700 flex items-center space-x-4">
            <div className="p-3.5 bg-emerald-50 dark:bg-emerald-900/40 rounded-xl text-emerald-600 dark:text-emerald-400">
              <CheckCircle className="h-6 w-6" />
            </div>
            <div>
              <div className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase">Assessments Done</div>
              <div className="text-2xl font-bold text-slate-900 dark:text-white">{loading ? '...' : stats?.assessmentsCompleted}</div>
              <div className="text-xs text-slate-500">Skill Gap Reports: {stats?.skillGapReports}</div>
            </div>
          </div>

          <div className="bg-white dark:bg-slate-800 rounded-xl p-5 shadow-sm border border-slate-200 dark:border-slate-700 flex items-center space-x-4">
            <div className="p-3.5 bg-purple-50 dark:bg-purple-900/40 rounded-xl text-purple-600 dark:text-purple-400">
              <Zap className="h-6 w-6" />
            </div>
            <div>
              <div className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase">AI Token Usage</div>
              <div className="text-2xl font-bold text-slate-900 dark:text-white">{loading ? '...' : stats?.aiTokenConsumptionToday?.toLocaleString()}</div>
              <div className="text-xs text-slate-500">Chat Sessions: {stats?.chatSessions}</div>
            </div>
          </div>

          <div className="bg-white dark:bg-slate-800 rounded-xl p-5 shadow-sm border border-slate-200 dark:border-slate-700 flex items-center space-x-4">
            <div className="p-3.5 bg-cyan-50 dark:bg-cyan-900/40 rounded-xl text-cyan-600 dark:text-cyan-400">
              <Activity className="h-6 w-6" />
            </div>
            <div>
              <div className="text-xs font-semibold text-slate-500 dark:text-slate-400 uppercase">System Status</div>
              <div className="text-xl font-bold text-emerald-600 dark:text-emerald-400 flex items-center">
                <span className="h-2.5 w-2.5 bg-emerald-500 rounded-full mr-2 animate-ping" />
                {stats?.systemHealth || 'HEALTHY'}
              </div>
              <div className="text-xs text-slate-500">Latency: {stats?.apiLatencyMs}ms</div>
            </div>
          </div>
        </div>

        {/* Popular Aggregations Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Popular Careers */}
          <div className="bg-white dark:bg-slate-800 rounded-xl p-5 shadow-sm border border-slate-200 dark:border-slate-700">
            <div className="flex items-center space-x-2 pb-3 mb-3 border-b border-slate-100 dark:border-slate-700">
              <Briefcase className="h-5 w-5 text-indigo-500" />
              <h3 className="font-semibold text-slate-800 dark:text-white">Most Popular Careers</h3>
            </div>
            <div className="space-y-3">
              {stats?.mostPopularCareers?.map((career, idx) => (
                <div key={idx} className="flex items-center justify-between text-sm p-2 hover:bg-slate-50 dark:hover:bg-slate-750 rounded-lg">
                  <div>
                    <div className="font-medium text-slate-800 dark:text-slate-200">{career.name}</div>
                    <div className="text-xs text-slate-400">{career.category}</div>
                  </div>
                  <span className="px-2.5 py-1 bg-indigo-50 dark:bg-indigo-900/50 text-indigo-600 dark:text-indigo-300 font-semibold rounded-full text-xs">
                    {career.count} views
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Popular Colleges */}
          <div className="bg-white dark:bg-slate-800 rounded-xl p-5 shadow-sm border border-slate-200 dark:border-slate-700">
            <div className="flex items-center space-x-2 pb-3 mb-3 border-b border-slate-100 dark:border-slate-700">
              <GraduationCap className="h-5 w-5 text-emerald-500" />
              <h3 className="font-semibold text-slate-800 dark:text-white">Most Viewed Colleges</h3>
            </div>
            <div className="space-y-3">
              {stats?.mostPopularColleges?.map((college, idx) => (
                <div key={idx} className="flex items-center justify-between text-sm p-2 hover:bg-slate-50 dark:hover:bg-slate-750 rounded-lg">
                  <div>
                    <div className="font-medium text-slate-800 dark:text-slate-200">{college.name}</div>
                    <div className="text-xs text-slate-400">NIRF Rank: #{college.nirfRank}</div>
                  </div>
                  <span className="px-2.5 py-1 bg-emerald-50 dark:bg-emerald-900/50 text-emerald-600 dark:text-emerald-300 font-semibold rounded-full text-xs">
                    {college.views} views
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Popular Scholarships */}
          <div className="bg-white dark:bg-slate-800 rounded-xl p-5 shadow-sm border border-slate-200 dark:border-slate-700">
            <div className="flex items-center space-x-2 pb-3 mb-3 border-b border-slate-100 dark:border-slate-700">
              <Award className="h-5 w-5 text-amber-500" />
              <h3 className="font-semibold text-slate-800 dark:text-white">Top Scholarships</h3>
            </div>
            <div className="space-y-3">
              {stats?.mostPopularScholarships?.map((scholarship, idx) => (
                <div key={idx} className="flex items-center justify-between text-sm p-2 hover:bg-slate-50 dark:hover:bg-slate-750 rounded-lg">
                  <div>
                    <div className="font-medium text-slate-800 dark:text-slate-200">{scholarship.name}</div>
                    <div className="text-xs text-amber-600 dark:text-amber-400 font-medium">{scholarship.amount}</div>
                  </div>
                  <span className="px-2.5 py-1 bg-amber-50 dark:bg-amber-900/50 text-amber-600 dark:text-amber-300 font-semibold rounded-full text-xs">
                    {scholarship.applications} apps
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Server & Resource Usage */}
        <div className="bg-white dark:bg-slate-800 rounded-xl p-5 shadow-sm border border-slate-200 dark:border-slate-700">
          <h3 className="font-semibold text-slate-800 dark:text-white mb-4">Infrastructure & Resource Utilization</h3>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-5">
            <div>
              <div className="flex justify-between text-xs text-slate-500 mb-1">
                <span className="flex items-center"><Cpu className="h-3.5 w-3.5 mr-1" /> CPU Usage</span>
                <span>{stats?.cpuUsagePercent}%</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                <div className="bg-indigo-600 h-full" style={{ width: `${stats?.cpuUsagePercent}%` }} />
              </div>
            </div>

            <div>
              <div className="flex justify-between text-xs text-slate-500 mb-1">
                <span className="flex items-center"><HardDrive className="h-3.5 w-3.5 mr-1" /> Memory (RAM)</span>
                <span>{stats?.memoryUsagePercent}%</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                <div className="bg-emerald-500 h-full" style={{ width: `${stats?.memoryUsagePercent}%` }} />
              </div>
            </div>

            <div>
              <div className="flex justify-between text-xs text-slate-500 mb-1">
                <span className="flex items-center"><HardDrive className="h-3.5 w-3.5 mr-1" /> Disk Storage</span>
                <span>{stats?.storageUsageGb} GB ({stats?.diskUsagePercent}%)</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                <div className="bg-purple-500 h-full" style={{ width: `${stats?.diskUsagePercent}%` }} />
              </div>
            </div>

            <div>
              <div className="flex justify-between text-xs text-slate-500 mb-1">
                <span className="flex items-center"><Clock className="h-3.5 w-3.5 mr-1" /> API Requests Today</span>
                <span>{stats?.apiUsageRequestsToday?.toLocaleString()}</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                <div className="bg-cyan-500 h-full" style={{ width: '65%' }} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </AdminLayout>
  );
};
