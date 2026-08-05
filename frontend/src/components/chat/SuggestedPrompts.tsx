import React from 'react';

interface SuggestedPromptsProps {
  onSelectPrompt: (promptText: string) => void;
}

const STARTER_PROMPTS = [
  {
    category: 'Career Guidance',
    prompt: 'What career suits me best based on my assessment results?',
    icon: '🎯'
  },
  {
    category: 'College Search',
    prompt: 'Which colleges accept JEE Main rank under 10,000?',
    icon: '🏫'
  },
  {
    category: 'Scholarships',
    prompt: 'Am I eligible for National Scholarship Portal (NSP) schemes?',
    icon: '🎓'
  },
  {
    category: 'Skill Gap',
    prompt: 'What technical skills am I currently missing for a Data Scientist role?',
    icon: '⚡'
  },
  {
    category: 'Resume Advice',
    prompt: 'How can I optimize my resume bullet points for ATS compatibility?',
    icon: '📄'
  },
  {
    category: 'Roadmap',
    prompt: 'Can you generate a 3-month step-by-step learning roadmap for me?',
    icon: '🗺️'
  }
];

export const SuggestedPrompts: React.FC<SuggestedPromptsProps> = ({ onSelectPrompt }) => {
  return (
    <div className="max-w-2xl mx-auto p-4 my-auto">
      <div className="text-center mb-6">
        <h3 className="text-xl font-bold text-gray-800">Welcome to your AI Career Mentor</h3>
        <p className="text-sm text-gray-500 mt-1">
          Ask me anything about your career path, college admissions, scholarships, skill gaps, or resume!
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {STARTER_PROMPTS.map((item, idx) => (
          <button
            key={idx}
            onClick={() => onSelectPrompt(item.prompt)}
            className="text-left p-3.5 bg-white border border-gray-200 rounded-xl hover:border-indigo-500 hover:shadow-md transition-all duration-200 group"
          >
            <div className="flex items-center space-x-2 text-xs font-semibold text-indigo-600 mb-1">
              <span>{item.icon}</span>
              <span>{item.category}</span>
            </div>
            <p className="text-sm text-gray-700 font-medium group-hover:text-indigo-900">
              "{item.prompt}"
            </p>
          </button>
        ))}
      </div>
    </div>
  );
};
