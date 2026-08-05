import React, { useState } from 'react';

interface FeedbackDialogProps {
  messageId: string;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (rating: number, feedbackText: string) => void;
}

export const FeedbackDialog: React.FC<FeedbackDialogProps> = ({
  isOpen,
  onClose,
  onSubmit
}) => {
  const [rating, setRating] = useState(5);
  const [feedbackText, setFeedbackText] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(rating, feedbackText);
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl p-6 max-w-md w-full shadow-2xl">
        <h3 className="text-lg font-bold text-gray-900 mb-2">Provide Feedback</h3>
        <p className="text-sm text-gray-500 mb-4">
          How helpful was this answer? Your feedback helps improve our AI Career Mentor.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="flex justify-center space-x-2">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                type="button"
                onClick={() => setRating(star)}
                className={`text-2xl transition-transform ${
                  star <= rating ? 'scale-110 text-yellow-400' : 'text-gray-300'
                }`}
              >
                ★
              </button>
            ))}
          </div>

          <div>
            <textarea
              rows={3}
              value={feedbackText}
              onChange={(e) => setFeedbackText(e.target.value)}
              placeholder="What did you like or what could be improved? (Optional)"
              className="w-full border border-gray-300 rounded-xl p-3 text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>

          <div className="flex justify-end space-x-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-xl"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 rounded-xl"
            >
              Submit Feedback
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
