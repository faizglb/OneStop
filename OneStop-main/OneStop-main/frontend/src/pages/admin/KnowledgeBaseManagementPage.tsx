import React, { useEffect, useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { adminService, KnowledgeDocument } from '@/services/adminService';
import { Database, Upload, RefreshCw, Trash2, FileText, CheckCircle2 } from 'lucide-react';

export const KnowledgeBaseManagementPage: React.FC = () => {
  const [docs, setDocs] = useState<KnowledgeDocument[]>([]);
  const [loading, setLoading] = useState(true);
  const [reindexing, setReindexing] = useState(false);
  const [category, setCategory] = useState('CAREERS');

  const loadDocs = async () => {
    setLoading(true);
    try {
      const data = await adminService.getKnowledgeDocs();
      setDocs(data);
    } catch (err) {
      console.error('Failed to load knowledge base docs:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDocs();
  }, []);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    try {
      await adminService.uploadKnowledgeDoc(file, category);
      loadDocs();
      alert(`Successfully uploaded and indexed ${file.name}`);
    } catch (err) {
      alert('File upload failed');
    }
  };

  const handleReindex = async () => {
    setReindexing(true);
    try {
      await adminService.reindexKnowledgeBase();
      alert('Knowledge base re-indexing task dispatched to vector engine!');
      loadDocs();
    } catch (err) {
      alert('Reindexing failed');
    } finally {
      setReindexing(false);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await adminService.deleteKnowledgeDoc(id);
      loadDocs();
    } catch (err) {
      alert('Delete failed');
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <Database className="h-6 w-6 mr-2 text-indigo-600" /> AI Knowledge Base Management (RAG Vector Store)
            </h1>
            <p className="text-sm text-slate-500">Ingest, chunk, embed, and refresh domain documents for the RAG Career Mentor Chatbot.</p>
          </div>

          <button
            onClick={handleReindex}
            className="flex items-center space-x-2 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-sm font-medium shadow"
          >
            <RefreshCw className={`h-4 w-4 ${reindexing ? 'animate-spin' : ''}`} />
            <span>Re-Index Vector DB</span>
          </button>
        </div>

        {/* Upload Box */}
        <div className="bg-white dark:bg-slate-800 p-6 rounded-xl shadow-sm border-2 border-dashed border-indigo-200 dark:border-indigo-900 text-center space-y-4">
          <FileText className="h-10 w-10 text-indigo-500 mx-auto" />
          <div>
            <h3 className="font-semibold text-slate-800 dark:text-white">Upload Training Documents (PDF, DOCX, CSV, JSON)</h3>
            <p className="text-xs text-slate-400">Documents will be chunked, embedded using Google Gemini text-embedding-004, and stored in vector DB.</p>
          </div>

          <div className="flex justify-center items-center space-x-4 max-w-sm mx-auto">
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="p-2 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-xs"
            >
              <option value="CAREERS">CAREERS</option>
              <option value="COLLEGES">COLLEGES</option>
              <option value="SCHOLARSHIPS">SCHOLARSHIPS</option>
              <option value="RESUMES">RESUMES</option>
              <option value="SKILLS">SKILLS</option>
              <option value="FAQ">FAQ</option>
            </select>

            <label className="cursor-pointer px-4 py-2 bg-indigo-600 text-white rounded-lg text-xs font-semibold hover:bg-indigo-700">
              <span>Choose File & Upload</span>
              <input type="file" onChange={handleFileUpload} accept=".pdf,.docx,.csv,.json" className="hidden" />
            </label>
          </div>
        </div>

        {/* Documents Table */}
        <div className="bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 dark:bg-slate-900/50 text-slate-500 font-semibold border-b border-slate-200 dark:border-slate-700">
              <tr>
                <th className="p-4">Document File</th>
                <th className="p-4">Category</th>
                <th className="p-4">Format</th>
                <th className="p-4">File Size</th>
                <th className="p-4">Vector Chunks</th>
                <th className="p-4">Status</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-700/50">
              {docs.map((d) => (
                <tr key={d.id} className="hover:bg-slate-50 dark:hover:bg-slate-750">
                  <td className="p-4 font-semibold text-slate-900 dark:text-white flex items-center">
                    <FileText className="h-4 w-4 mr-2 text-indigo-500" />
                    {d.filename}
                  </td>
                  <td className="p-4"><span className="px-2 py-0.5 bg-slate-100 dark:bg-slate-700 text-xs font-mono font-medium rounded">{d.docCategory}</span></td>
                  <td className="p-4 text-xs font-bold">{d.fileType}</td>
                  <td className="p-4 text-xs font-mono">{(d.fileSize / 1024).toFixed(1)} KB</td>
                  <td className="p-4 text-xs font-semibold text-indigo-600">{d.chunkCount} chunks</td>
                  <td className="p-4">
                    <span className="inline-flex items-center text-xs font-semibold text-emerald-600 bg-emerald-50 dark:bg-emerald-900/30 px-2.5 py-1 rounded-full">
                      <CheckCircle2 className="h-3 w-3 mr-1" /> {d.status}
                    </span>
                  </td>
                  <td className="p-4 text-right">
                    <button onClick={() => handleDelete(d.id)} className="p-1.5 text-rose-600 hover:bg-rose-50 rounded">
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
