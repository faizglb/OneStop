import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import {
  LayoutDashboard,
  Users,
  ShieldAlert,
  Briefcase,
  GraduationCap,
  Award,
  BookOpen,
  CheckSquare,
  GitFork,
  Database,
  Sliders,
  BarChart3,
  FileText,
  Bell,
  LogOut,
  Moon,
  Sun,
  Menu,
  X,
  Sparkles
} from 'lucide-react';

interface AdminLayoutProps {
  children: React.ReactNode;
}

export const AdminLayout: React.FC<AdminLayoutProps> = ({ children }) => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [darkMode, setDarkMode] = useState(false);

  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
    document.documentElement.classList.toggle('dark');
  };

  const menuItems = [
    { path: '/admin/dashboard', label: 'Overview Dashboard', icon: LayoutDashboard },
    { path: '/admin/users', label: 'User Management', icon: Users },
    { path: '/admin/roles', label: 'Role & Permissions (RBAC)', icon: ShieldAlert },
    { path: '/admin/careers', label: 'Career Catalog', icon: Briefcase },
    { path: '/admin/colleges', label: 'College Directory', icon: GraduationCap },
    { path: '/admin/scholarships', label: 'Scholarship Finder', icon: Award },
    { path: '/admin/assessments', label: 'Assessment Bank', icon: CheckSquare },
    { path: '/admin/learning-resources', label: 'Learning Resources', icon: BookOpen },
    { path: '/admin/roadmaps', label: 'Roadmap Templates', icon: GitFork },
    { path: '/admin/knowledge-base', label: 'AI Knowledge Base (RAG)', icon: Database },
    { path: '/admin/ai-config', label: 'AI LLM Config', icon: Sliders },
    { path: '/admin/analytics', label: 'Analytics & Trends', icon: BarChart3 },
    { path: '/admin/audit-logs', label: 'System Audit Logs', icon: FileText },
    { path: '/admin/notifications', label: 'Notifications & Alerts', icon: Bell },
  ];

  return (
    <div className={`min-h-screen ${darkMode ? 'dark bg-slate-900 text-slate-100' : 'bg-slate-50 text-slate-800'} flex flex-col`}>
      {/* Top Navbar */}
      <header className="sticky top-0 z-30 bg-indigo-900 text-white border-b border-indigo-800 shadow-md px-4 py-3 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="md:hidden p-2 rounded-lg hover:bg-indigo-800 focus:outline-none"
          >
            {sidebarOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
          </button>
          <div className="flex items-center space-x-2">
            <Sparkles className="h-7 w-7 text-cyan-400 animate-pulse" />
            <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-cyan-400 to-indigo-200 bg-clip-text text-transparent">
              ACEA Admin Command Center
            </span>
          </div>
        </div>

        <div className="flex items-center space-x-4">
          <button
            onClick={toggleDarkMode}
            className="p-2 rounded-full hover:bg-indigo-800 transition-colors"
            title="Toggle Dark Mode"
          >
            {darkMode ? <Sun className="h-5 w-5 text-amber-400" /> : <Moon className="h-5 w-5 text-slate-200" />}
          </button>

          <div className="hidden sm:flex items-center space-x-3 border-l border-indigo-700 pl-4">
            <div className="text-right">
              <div className="text-sm font-semibold">{user?.fullName || user?.email || 'Administrator'}</div>
              <div className="text-xs text-cyan-300 font-mono">
                [{user?.role || 'SUPER_ADMIN'}]
              </div>
            </div>
            <div className="h-9 w-9 rounded-full bg-cyan-500 flex items-center justify-center font-bold text-slate-900 shadow">
              {(user?.firstName?.[0] || 'A').toUpperCase()}
            </div>
          </div>

          <button
            onClick={() => {
              logout();
              navigate('/login');
            }}
            className="p-2 text-rose-300 hover:text-white hover:bg-rose-900/50 rounded-lg transition-colors flex items-center space-x-1 text-sm font-medium"
            title="Sign Out"
          >
            <LogOut className="h-5 w-5" />
            <span className="hidden md:inline">Exit</span>
          </button>
        </div>
      </header>

      <div className="flex flex-1 overflow-hidden">
        {/* Sidebar Navigation */}
        <aside
          className={`${
            sidebarOpen ? 'translate-x-0' : '-translate-x-full'
          } md:translate-x-0 fixed md:relative z-20 w-64 h-[calc(100vh-61px)] bg-slate-900 text-slate-300 transition-transform duration-200 ease-in-out border-r border-slate-800 overflow-y-auto flex flex-col justify-between`}
        >
          <div className="p-3 space-y-1">
            <div className="px-3 py-2 text-xs font-semibold text-slate-400 uppercase tracking-wider">
              Administration Modules
            </div>
            {menuItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  onClick={() => setSidebarOpen(false)}
                  className={`flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                    isActive
                      ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                      : 'hover:bg-slate-800 hover:text-white text-slate-400'
                  }`}
                >
                  <Icon className={`h-4 w-4 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                  <span>{item.label}</span>
                </Link>
              );
            })}
          </div>

          <div className="p-4 border-t border-slate-800 text-xs text-slate-400 text-center">
            <div>ACEA Platform v1.12.0</div>
            <div className="text-emerald-400 mt-1">● Environment: Production</div>
          </div>
        </aside>

        {/* Main Content Area */}
        <main className="flex-1 overflow-y-auto p-6">
          {children}
        </main>
      </div>
    </div>
  );
};
