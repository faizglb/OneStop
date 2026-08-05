import React, { useEffect, useState } from 'react';
import { AdminLayout } from '@/components/admin/AdminLayout';
import { adminService, AiConfig } from '@/services/adminService';
import { Sliders, Cpu, Save, RefreshCw } from 'lucide-react';

export const AiConfigPage: React.FC = () => {
  const [config, setConfig] = useState<AiConfig>({
    llmProvider: 'GEMINI',
    geminiModel: 'gemini-1.5-pro',
    openAiModel: 'gpt-4o',
    temperature: 0.7,
    topP: 0.95,
    maxTokens: 2048,
    embeddingModel: 'text-embedding-004',
    similarityThreshold: 0.75,
    chunkingStrategy: 'SEMANTIC_PARAGRAPH'
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const loadConfig = async () => {
    setLoading(true);
    try {
      const data = await adminService.getAiConfig();
      setConfig(data);
    } catch (err) {
      console.error('Failed to load AI config:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadConfig();
  }, []);

  const handleSave = async () => {
    setSaving(true);
    try {
      const updated = await adminService.updateAiConfig(config);
      setConfig(updated);
      alert('AI Configuration updated successfully across all microservices!');
    } catch (err) {
      alert('Failed to save AI configuration');
    } finally {
      setSaving(false);
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6 max-w-4xl">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center">
              <Sliders className="h-6 w-6 mr-2 text-indigo-600" /> AI Engine & LLM Configuration
            </h1>
            <p className="text-sm text-slate-500">Configure LLM providers, model selection, temperature, tokens, and RAG thresholds.</p>
          </div>
          <button
            onClick={handleSave}
            disabled={saving}
            className="flex items-center space-x-2 px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium shadow transition-all self-start sm:self-auto"
          >
            <Save className="h-4 w-4" />
            <span>{saving ? 'Saving...' : 'Save Configuration'}</span>
          </button>
        </div>

        {/* Primary Provider Selector */}
        <div className="bg-white dark:bg-slate-800 p-6 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 space-y-4">
          <h3 className="text-lg font-semibold text-slate-900 dark:text-white flex items-center">
            <Cpu className="h-5 w-5 mr-2 text-indigo-500" /> Active LLM Provider
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div
              onClick={() => setConfig({ ...config, llmProvider: 'GEMINI' })}
              className={`p-4 rounded-xl border-2 cursor-pointer transition-all ${
                config.llmProvider === 'GEMINI'
                  ? 'border-indigo-600 bg-indigo-50/50 dark:bg-indigo-900/30'
                  : 'border-slate-200 dark:border-slate-700'
              }`}
            >
              <div className="font-bold text-slate-900 dark:text-white">Google Gemini (Recommended)</div>
              <div className="text-xs text-slate-500 mt-1">High speed, cost-effective multimodal reasoning & native embeddings.</div>
            </div>

            <div
              onClick={() => setConfig({ ...config, llmProvider: 'OPENAI' })}
              className={`p-4 rounded-xl border-2 cursor-pointer transition-all ${
                config.llmProvider === 'OPENAI'
                  ? 'border-indigo-600 bg-indigo-50/50 dark:bg-indigo-900/30'
                  : 'border-slate-200 dark:border-slate-700'
              }`}
            >
              <div className="font-bold text-slate-900 dark:text-white">OpenAI GPT-4o</div>
              <div className="text-xs text-slate-500 mt-1">Enterprise reasoning with OpenAI API suite integration.</div>
            </div>
          </div>
        </div>

        {/* Model Hyperparameters */}
        <div className="bg-white dark:bg-slate-800 p-6 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 space-y-6">
          <h3 className="text-lg font-semibold text-slate-900 dark:text-white">Hyperparameter Controls</h3>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                Temperature ({config.temperature})
              </label>
              <input
                type="range"
                min="0.0"
                max="1.0"
                step="0.05"
                value={config.temperature}
                onChange={(e) => setConfig({ ...config, temperature: parseFloat(e.target.value) })}
                className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer"
              />
              <div className="flex justify-between text-xs text-slate-400 mt-1">
                <span>0.0 (Precise / Factual)</span>
                <span>1.0 (Creative)</span>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                Top-P Sampling ({config.topP})
              </label>
              <input
                type="range"
                min="0.5"
                max="1.0"
                step="0.05"
                value={config.topP}
                onChange={(e) => setConfig({ ...config, topP: parseFloat(e.target.value) })}
                className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                Max Response Tokens
              </label>
              <input
                type="number"
                value={config.maxTokens}
                onChange={(e) => setConfig({ ...config, maxTokens: parseInt(e.target.value) || 2048 })}
                className="w-full p-2.5 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-lg text-sm"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                RAG Cosine Similarity Threshold ({config.similarityThreshold})
              </label>
              <input
                type="range"
                min="0.5"
                max="0.95"
                step="0.05"
                value={config.similarityThreshold}
                onChange={(e) => setConfig({ ...config, similarityThreshold: parseFloat(e.target.value) })}
                className="w-full h-2 bg-slate-200 rounded-lg appearance-none cursor-pointer"
              />
            </div>
          </div>
        </div>
      </div>
    </AdminLayout>
  );
};
