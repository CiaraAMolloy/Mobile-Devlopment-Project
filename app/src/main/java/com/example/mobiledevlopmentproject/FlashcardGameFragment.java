package com.example.mobiledevlopmentproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;

public class FlashcardGameFragment extends Fragment {

    private static final String ARG_SET_NAME = "set_name";
    private static final String ARG_MODE = "mode";
    private static final String ARG_LIMIT_SECONDS = "limit_seconds";

    private Button btnShare;



    /**
     * Two game modes:
     * FINISH_ALL  - answer until every card has been answered correctly at least once,
     *               measure total time.
     * FIXED_TIME  - fixed time limit (15/30/45 seconds); count how many answers are correct.
     */
    public enum GameMode {
        FINISH_ALL,
        FIXED_TIME
    }

    /**
     * Internal game status:
     * READY    - includes the 3-second pre-countdown.
     * RUNNING  - timer is active, user can answer.
     * PAUSED   - timer stopped, user cannot answer.
     * FINISHED - game is over, results are shown.
     */
    private enum GameStatus {
        READY,
        RUNNING,
        PAUSED,
        FINISHED
    }

    // Arguments
    private String setName;
    private GameMode gameMode;
    private int limitSecondsForDisplay = 0;
    private long limitMillis = 0L; // used only for FIXED_TIME mode

    // Card list
    private final ArrayList<FlashCard> cards = new ArrayList<>();

    // FINISH_ALL mode: we use a queue of indices
    private final ArrayList<Integer> questionQueue = new ArrayList<>();
    private int queuePosition = 0;
    private boolean[] answeredCorrectOnce;
    private int correctFirstTimeCount = 0;

    // FIXED_TIME mode: single pass through the shuffled list
    private int currentIndex = 0;

    // Common statistics
    private int totalCards = 0;
    private int correctCount = 0;
    private int wrongCount = 0;
    private int attemptCount = 0;

    // Timer state (excluding paused time)
    private long elapsedBeforePause = 0L;
    private long lastResumeTime = 0L;
    private GameStatus status = GameStatus.READY;

    // Timer handler for updating the on-screen clock
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (status == GameStatus.RUNNING) {
                updateTimerUI();
                // Update every 0.5 seconds
                timerHandler.postDelayed(this, 500);
            }
        }
    };

    // 3-second pre-countdown before the game actually starts
    private final Handler preHandler = new Handler(Looper.getMainLooper());
    private int preSecondsLeft = 3;
    private final Runnable preCountdownRunnable = new Runnable() {
        @Override
        public void run() {
            preSecondsLeft--;
            if (preSecondsLeft > 0) {
                if (tvCountdown != null) {
                    tvCountdown.setText(String.valueOf(preSecondsLeft));
                }
                preHandler.postDelayed(this, 1000);
            } else {
                if (tvCountdown != null) {
                    tvCountdown.setText("Go!");
                }
                // Small delay to show "Go!" and then hide it
                preHandler.postDelayed(() -> {
                    if (tvCountdown != null) {
                        tvCountdown.setVisibility(View.GONE);
                    }
                    beginRunningGame();
                }, 500);
            }
        }
    };

    // Views
    private TextView tvMode;
    private TextView tvQuestion;
    private TextView tvTimer;
    private TextView tvProgress;
    private TextView tvFeedback;
    private TextView tvCountdown;
    private EditText etAnswer;
    private Button btnCheck;
    private Button btnPause;

    public FlashcardGameFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method.
     *
     * @param setName      name of the set to play.
     * @param mode         game mode (FINISH_ALL or FIXED_TIME).
     * @param limitSeconds time limit in seconds for FIXED_TIME mode; for FINISH_ALL pass 0.
     */
    public static FlashcardGameFragment newInstance(String setName, GameMode mode, int limitSeconds) {
        FlashcardGameFragment fragment = new FlashcardGameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SET_NAME, setName);
        args.putString(ARG_MODE, mode.name());
        args.putInt(ARG_LIMIT_SECONDS, limitSeconds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setName = getArguments().getString(ARG_SET_NAME);
            String modeName = getArguments().getString(ARG_MODE, GameMode.FINISH_ALL.name());
            gameMode = GameMode.valueOf(modeName);
            limitSecondsForDisplay = getArguments().getInt(ARG_LIMIT_SECONDS, 0);
            if (limitSecondsForDisplay > 0) {
                limitMillis = limitSecondsForDisplay * 1000L;
            } else {
                limitMillis = 0L;
            }
        } else {
            gameMode = GameMode.FINISH_ALL;
            limitMillis = 0L;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // You must create fragment_flashcard_game.xml with the required views.
        return inflater.inflate(R.layout.fragment_flashcard_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMode = view.findViewById(R.id.tvMode);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        tvTimer = view.findViewById(R.id.tvTimer);
        tvProgress = view.findViewById(R.id.tvProgress);
        tvFeedback = view.findViewById(R.id.tvFeedback);
        tvCountdown = view.findViewById(R.id.tvCountdown);
        etAnswer = view.findViewById(R.id.etAnswer);
        btnCheck = view.findViewById(R.id.btnCheck);
        btnPause = view.findViewById(R.id.btnPause);

        // Show mode information at the top
        if (gameMode == GameMode.FINISH_ALL) {
            tvMode.setText("Finish all cards");
        } else {
            tvMode.setText("Timed game (" + limitSecondsForDisplay + " s)");
        }

        btnCheck.setOnClickListener(v -> onCheckAnswer());
        btnPause.setOnClickListener(v -> onPauseResumeClicked());
        btnShare = view.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(v -> shareScore());

        // When the fragment is first shown, user cannot answer yet.
        // The game starts only after the 3-second pre-countdown finishes.
        etAnswer.setEnabled(false);
        btnCheck.setEnabled(false);
        btnPause.setEnabled(false);

        loadCardsForSet();

        if (totalCards == 0) {
            Toast.makeText(getContext(), "No cards in this set", Toast.LENGTH_SHORT).show();
            status = GameStatus.FINISHED;
            return;
        }

        status = GameStatus.READY;
        correctCount = wrongCount = attemptCount = 0;
        correctFirstTimeCount = 0;

        // Show the first question (without starting the timer yet),
        // then start the 3-second countdown.
        showCurrentCard();
        startPreCountdown();
    }

    /**
     * Starts the 3-second on-screen countdown: 3 -> 2 -> 1 -> Go!
     * Real timing starts only after this countdown is finished.
     */
    private void startPreCountdown() {
        preSecondsLeft = 3;
        if (tvCountdown != null) {
            tvCountdown.setVisibility(View.VISIBLE);
            tvCountdown.setText(String.valueOf(preSecondsLeft));
        }
        preHandler.removeCallbacks(preCountdownRunnable);
        preHandler.postDelayed(preCountdownRunnable, 1000);
    }

    /**
     * Called when the 3-second countdown finishes.
     * Enables input and starts the real game timer.
     */
    private void beginRunningGame() {
        status = GameStatus.RUNNING;
        elapsedBeforePause = 0L;
        lastResumeTime = SystemClock.elapsedRealtime();

        etAnswer.setEnabled(true);
        btnCheck.setEnabled(true);
        btnPause.setEnabled(true);

        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.post(timerRunnable);
        updateTimerUI();
    }

    /**
     * Load all cards for the given set from the database.
     */
    private void loadCardsForSet() {
        if (getContext() == null || TextUtils.isEmpty(setName)) return;

        DBFlashCardStore store = new DBFlashCardStore(getContext());
        ArrayList<FlashCard> allCards = store.getFlashCards(getContext());

        cards.clear();
        if (allCards != null) {
            for (FlashCard card : allCards) {
                if (card != null && setName.equals(card.getSetName())) {
                    cards.add(card);
                }
            }
        }
        totalCards = cards.size();

        if (totalCards == 0) {
            return;
        }

        if (gameMode == GameMode.FINISH_ALL) {
            questionQueue.clear();
            for (int i = 0; i < totalCards; i++) {
                questionQueue.add(i);
            }
            Collections.shuffle(questionQueue);
            queuePosition = 0;
            answeredCorrectOnce = new boolean[totalCards];
        } else {
            Collections.shuffle(cards);
            currentIndex = 0;
        }
    }

    /**
     * Show the current question based on the current mode.
     * This does not depend on whether the timer is running or not.
     */
    private void showCurrentCard() {
        if (etAnswer != null) {
            etAnswer.setText("");
        }
        if (tvFeedback != null) {
            tvFeedback.setText("");
        }

        if (gameMode == GameMode.FINISH_ALL) {
            if (questionQueue.isEmpty() || queuePosition >= questionQueue.size()) {
                if (tvQuestion != null) tvQuestion.setText("No more cards");
                if (tvProgress != null) tvProgress.setText("");
                return;
            }
            int cardIndex = questionQueue.get(queuePosition);
            FlashCard card = cards.get(cardIndex);
            if (tvQuestion != null) tvQuestion.setText(card.getTerm());
            if (tvProgress != null) {
                tvProgress.setText(correctFirstTimeCount + "/" + totalCards);
            }
        } else {
            if (currentIndex >= totalCards) {
                if (tvQuestion != null) tvQuestion.setText("All cards finished");
                if (tvProgress != null) {
                    tvProgress.setText(totalCards + "/" + totalCards);
                }
                return;
            }
            FlashCard card = cards.get(currentIndex);
            if (tvQuestion != null) tvQuestion.setText(card.getTerm());
            if (tvProgress != null) {
                tvProgress.setText(currentIndex + "/" + totalCards);
            }
        }
    }

    /**
     * Called when the user taps the "Check" button.
     */
    private void onCheckAnswer() {
        if (status != GameStatus.RUNNING) return;
        if (cards.isEmpty()) return;

        String answer = etAnswer.getText().toString().trim();
        if (TextUtils.isEmpty(answer)) {
            etAnswer.setError("Please enter an answer");
            return;
        }

        if (gameMode == GameMode.FINISH_ALL) {
            handleCheckFinishAll(answer);
        } else {
            handleCheckFixedTime(answer);
        }
    }

    /**
     * FINISH_ALL mode:
     * - We use a queue of card indices.
     * - If the answer is wrong, the current index is pushed to the end of the queue.
     * - The game ends when every card has been answered correctly at least once.
     */
    private void handleCheckFinishAll(String answer) {
        if (queuePosition >= questionQueue.size()) {
            finishFinishAll();
            return;
        }
        int cardIndex = questionQueue.get(queuePosition);
        FlashCard card = cards.get(cardIndex);

        attemptCount++;
        boolean isCorrect = answer.equalsIgnoreCase(card.getDef());
        if (isCorrect) {
            correctCount++;
            if (!answeredCorrectOnce[cardIndex]) {
                answeredCorrectOnce[cardIndex] = true;
                correctFirstTimeCount++;
            }
            showFeedback(true);
        } else {
            wrongCount++;
            showFeedback(false);
            // Put this card index at the end of the queue
            questionQueue.add(cardIndex);
        }

        // If every card has been answered correctly at least once, end the game.
        if (correctFirstTimeCount >= totalCards) {
            finishFinishAll();
            return;
        }

        queuePosition++;
        showCurrentCard();
    }

    /**
     * FIXED_TIME mode:
     * - We walk through the shuffled list exactly once.
     * - If all cards are done before time is up, we end early.
     * - If time runs out, we end immediately and the current question is not counted.
     */
    private void handleCheckFixedTime(String answer) {
        if (currentIndex >= totalCards) {
            finishFixedTime(true);
            return;
        }
        FlashCard card = cards.get(currentIndex);

        attemptCount++;
        boolean isCorrect = answer.equalsIgnoreCase(card.getDef());
        if (isCorrect) {
            correctCount++;
            showFeedback(true);
        } else {
            wrongCount++;
            showFeedback(false);
        }
        currentIndex++;

        if (currentIndex >= totalCards) {
            // Finished the whole deck before time is up
            finishFixedTime(true);
        } else {
            showCurrentCard();
        }
    }

    /**
     * Show a simple text feedback.
     * You can later replace this with icons (checkmark / cross).
     */
    private void showFeedback(boolean correct) {
        if (tvFeedback == null) return;
        if (correct) {
            tvFeedback.setText("✓ Correct");
        } else {
            tvFeedback.setText("✗ Wrong (will appear again)");
        }
    }

    /**
     * Handle pause/resume button.
     */
    private void onPauseResumeClicked() {
        if (status == GameStatus.RUNNING) {
            pauseGame();
        } else if (status == GameStatus.PAUSED) {
            resumeGame();
        }
    }

    /**
     * Pause the game: stop the timer but do NOT treat it as a loss.
     */
    private void pauseGame() {
        if (status != GameStatus.RUNNING) return;
        long now = SystemClock.elapsedRealtime();
        elapsedBeforePause += now - lastResumeTime;
        status = GameStatus.PAUSED;
        timerHandler.removeCallbacks(timerRunnable);
        if (tvFeedback != null) {
            tvFeedback.setText("Paused");
        }
    }

    /**
     * Resume the game from a paused state.
     */
    private void resumeGame() {
        if (status != GameStatus.PAUSED) return;
        status = GameStatus.RUNNING;
        lastResumeTime = SystemClock.elapsedRealtime();
        if (tvFeedback != null) {
            tvFeedback.setText("");
        }
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.post(timerRunnable);
    }

    /**
     * Get the total active time in milliseconds (excluding pauses).
     */
    private long getActiveMillis() {
        if (status == GameStatus.RUNNING) {
            long now = SystemClock.elapsedRealtime();
            return elapsedBeforePause + (now - lastResumeTime);
        } else {
            return elapsedBeforePause;
        }
    }

    /**
     * Update the timer text based on the current mode.
     * FINISH_ALL: show elapsed time.
     * FIXED_TIME: show remaining time; end the game when it reaches zero.
     */
    private void updateTimerUI() {
        long activeMillis = getActiveMillis();
        if (gameMode == GameMode.FINISH_ALL) {
            if (tvTimer != null) {
                tvTimer.setText(formatMillisToClock(activeMillis));
            }
        } else {
            long remaining = limitMillis - activeMillis;
            if (remaining <= 0L) {
                // Time is up: the current question is not counted.
                finishFixedTime(false);
                return;
            }
            if (tvTimer != null) {
                tvTimer.setText(formatMillisToClock(remaining));
            }
        }
    }

    /**
     * Convert milliseconds to a mm:ss string.
     */
    private String formatMillisToClock(long millis) {
        long totalSeconds = millis / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * End of FINISH_ALL mode:
     * show time, correct / wrong count, and lock the UI.
     */
    private void finishFinishAll() {
        if (status == GameStatus.FINISHED) return;
        elapsedBeforePause = getActiveMillis();
        status = GameStatus.FINISHED;
        timerHandler.removeCallbacks(timerRunnable);
        preHandler.removeCallbacks(preCountdownRunnable);

        long totalMillis = elapsedBeforePause;
        saveScoreToDB(correctCount + "/" + totalCards, formatMillisToClock(totalMillis));
        StringBuilder sb = new StringBuilder();
        sb.append("Finished all cards!\n");
        sb.append("Time: ").append(formatMillisToClock(totalMillis)).append("\n");
        sb.append("Correct: ").append(correctCount).append("/").append(totalCards).append("\n");
        sb.append("Wrong: ").append(wrongCount).append("\n");

        lockUIAndShowResult(sb.toString());
    }

    /**
     * End of FIXED_TIME mode:
     * show correct / wrong count and accuracy and lock the UI.
     *
     * @param finishedAllBeforeTimeUp true if the user completed all cards before time ran out.
     */
    private void finishFixedTime(boolean finishedAllBeforeTimeUp) {
        if (status == GameStatus.FINISHED) return;
        elapsedBeforePause = getActiveMillis();
        status = GameStatus.FINISHED;
        timerHandler.removeCallbacks(timerRunnable);
        preHandler.removeCallbacks(preCountdownRunnable);

        saveScoreToDB(correctCount + " (Timed)", formatMillisToClock(getActiveMillis()));

        StringBuilder sb = new StringBuilder();
        if (finishedAllBeforeTimeUp) {
            sb.append("Finished all cards before time!\n");
        } else {
            sb.append("Time is up!\n");
        }
        sb.append("Correct: ").append(correctCount).append("\n");
        sb.append("Wrong: ").append(wrongCount).append("\n");
        sb.append("Accuracy: ");
        if (attemptCount > 0) {
            int percent = (int) Math.round(100.0 * correctCount / (double) attemptCount);
            sb.append(percent).append("%");
        } else {
            sb.append("N/A");
        }

        lockUIAndShowResult(sb.toString());
    }

    /**
     * After the game finishes, disable input and show the result summary.
     */
    private void lockUIAndShowResult(String resultText) {
        if (etAnswer != null) etAnswer.setEnabled(false);
        if (btnCheck != null) btnCheck.setEnabled(false);
        if (btnPause != null) btnPause.setEnabled(false);
        if (tvQuestion != null) tvQuestion.setText("");
        if (tvProgress != null) tvProgress.setText("");
        if (tvFeedback != null) tvFeedback.setText(resultText);
        if (btnShare != null) {
            btnShare.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // If the app goes to background (home button, incoming call), pause the game automatically.
        if (status == GameStatus.RUNNING) {
            pauseGame();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timerHandler.removeCallbacks(timerRunnable);
        preHandler.removeCallbacks(preCountdownRunnable);
    }

    /**
     * Helper method to save the game result to the Leaderboard database.
     */
    private void saveScoreToDB(String scoreVal, String timeVal) {
        if (getContext() == null) return;

        // Get current system time
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDate = sdf.format(new java.util.Date());

        // Use the NEW separate database handler
        DBLeaderboard db = new DBLeaderboard(getContext());
        db.addScore(setName, scoreVal, timeVal, currentDate);

        // Optional: Show a toast to confirm saving
        Toast.makeText(getContext(), "Score Saved to Leaderboard!", Toast.LENGTH_SHORT).show();
    }


    /**
     * Share the game result using Android System Share Sheet.
     */
    private void shareScore() {
        // Get the feedback text (or build your own string)
        String scoreText = tvFeedback.getText().toString();

        String shareBody = "I just played Flashcards!\n" + scoreText + "\nCan you beat my score?";

        // Create the Intent
        android.content.Intent sharingIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Flashcard Score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        // Launch the system chooser
        startActivity(android.content.Intent.createChooser(sharingIntent, "Share via"));
    }
}
