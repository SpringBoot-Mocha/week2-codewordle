<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CodeWordle - Programming Game</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .animate-slide-up {
            animation: slideUp 0.3s ease-out;
        }

        .gradient-bg {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        .gradient-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        .gradient-btn:hover {
            filter: brightness(1.1);
        }

        .card-shadow {
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        }

        input[type="text"]::placeholder {
            color: #d1d5db;
        }
    </style>
</head>
<body class="gradient-bg min-h-screen flex items-center justify-center p-4 font-sans">
    <div class="w-full max-w-2xl">
        <div class="bg-white rounded-2xl card-shadow p-8 md:p-12">
            
            <!-- Game Header -->
            <div class="text-center mb-8">
                <h1 class="text-5xl font-bold text-indigo-600 mb-2">🎮 CodeWordle</h1>
                <p class="text-gray-600 text-lg">Guess programming terminology in 6 attempts!</p>
            </div>

            <!-- Loading Indicator -->
            <div id="loadingSpinner" class="hidden text-center py-8">
                <div class="inline-block">
                    <svg class="animate-spin h-8 w-8 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                </div>
                <p class="mt-3 text-gray-600">Loading...</p>
            </div>

            <!-- Theme Selector (Initial State) -->
            <div id="themeSelector" class="mb-6">
                <label for="themeSelect" class="block text-gray-700 font-semibold mb-3 text-lg">Select Theme:</label>
                <select id="themeSelect" class="w-full px-4 py-3 border-2 border-indigo-600 rounded-lg text-gray-800 font-medium focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:ring-offset-1 bg-white cursor-pointer">
                    <option value="">Choose a theme...</option>
                    <c:forEach var="theme" items="${themes}">
                        <option value="${theme.id}">${theme.name} - ${theme.description}</option>
                    </c:forEach>
                </select>
                <button id="startGameBtn" onclick="startGame()" disabled class="w-full mt-4 py-3 gradient-btn text-white font-semibold rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed hover:disabled:filter-none">
                    Start New Game
                </button>
            </div>

            <!-- Messages -->
            <div id="errorMessage" class="hidden mb-4 p-4 bg-red-100 border-l-4 border-red-500 text-red-700 rounded animate-slide-up"></div>
            <div id="successMessage" class="hidden mb-4 p-4 bg-green-100 border-l-4 border-green-500 text-green-700 rounded animate-slide-up"></div>

            <!-- Game Board -->
            <div id="gameBoard" class="hidden">
                
                <!-- Game Info -->
                <div class="grid grid-cols-3 gap-4 mb-6 p-4 bg-gray-100 rounded-lg">
                    <div class="text-center">
                        <p class="text-gray-600 text-sm">Attempts</p>
                        <p class="text-2xl font-bold text-indigo-600"><span id="currentAttempts">0</span>/<span id="maxAttempts">6</span></p>
                    </div>
                    <div class="text-center">
                        <p class="text-gray-600 text-sm">Word Length</p>
                        <p class="text-2xl font-bold text-indigo-600"><span id="wordLength">0</span> letters</p>
                    </div>
                    <div class="text-center">
                        <p class="text-gray-600 text-sm">Theme</p>
                        <p class="text-xl font-bold text-indigo-600"><span id="currentTheme">-</span></p>
                    </div>
                </div>

                <!-- Game Result -->
                <div id="gameResult" class="hidden mb-6 p-6 rounded-lg text-center animate-slide-up">
                    <h3 id="resultTitle" class="text-3xl font-bold mb-2"></h3>
                    <p id="resultMessage" class="text-lg"></p>
                </div>

                <!-- Attempts History -->
                <div id="attemptsContainer" class="mb-6 max-h-80 overflow-y-auto space-y-2"></div>

                <!-- Input Section -->
                <div id="inputSection" class="hidden mb-6">
                    <div class="flex gap-3">
                        <input type="text" id="guessInput" placeholder="Enter word..." maxlength="5" class="flex-1 px-4 py-3 border-2 border-gray-300 rounded-lg text-lg font-semibold uppercase focus:outline-none focus:border-indigo-600 focus:ring-2 focus:ring-indigo-600 focus:ring-offset-1 transition">
                        <button id="guessBtn" onclick="makeGuess()" class="px-6 py-3 gradient-btn text-white font-semibold rounded-lg transition hover:filter-brightness-110 disabled:opacity-50 disabled:cursor-not-allowed">
                            Guess
                        </button>
                    </div>
                </div>

                <!-- New Game Button -->
                <button id="newGameBtn" onclick="resetGame()" class="hidden w-full py-3 gradient-btn text-white font-semibold rounded-lg transition hover:filter-brightness-110">
                    New Game
                </button>
            </div>
        </div>
    </div>

    <script>
        let currentGameId = null;
        let gameStatus = null;

        const themeSelect = document.getElementById('themeSelect');
        const startGameBtn = document.getElementById('startGameBtn');
        const guessInput = document.getElementById('guessInput');
        const guessBtn = document.getElementById('guessBtn');

        themeSelect.addEventListener('change', function() {
            startGameBtn.disabled = !this.value;
        });

        guessInput.addEventListener('keypress', function(event) {
            if (event.key === 'Enter' && !guessBtn.disabled) {
                makeGuess();
            }
        });

        function startGame() {
            const themeId = themeSelect.value;
            if (!themeId) {
                showError('Please select a theme');
                return;
            }

            showLoading(true);
            startGameBtn.disabled = true;

            fetch('/api/games?themeId=' + parseInt(themeId), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    currentGameId = data.gameId;
                    document.getElementById('themeSelector').classList.add('hidden');
                    document.getElementById('gameBoard').classList.remove('hidden');
                    document.getElementById('inputSection').classList.remove('hidden');
                    document.getElementById('newGameBtn').classList.add('hidden');
                    document.getElementById('maxAttempts').textContent = data.maxAttempts;
                    document.getElementById('wordLength').textContent = data.wordLength;
                    document.getElementById('currentTheme').textContent = themeSelect.options[themeSelect.selectedIndex].text.split(' - ')[0];
                    document.getElementById('attemptsContainer').innerHTML = '';
                    document.getElementById('gameResult').classList.add('hidden');
                    guessInput.focus();
                    showLoading(false);
                } else {
                    showError(data.message);
                    showLoading(false);
                    startGameBtn.disabled = false;
                }
            })
            .catch(error => {
                showError('Error starting game: ' + error.message);
                showLoading(false);
                startGameBtn.disabled = false;
            });
        }

        function makeGuess() {
            const word = guessInput.value.trim();
            
            if (!word) {
                showError('Please enter a word');
                return;
            }

            if (!currentGameId) {
                showError('No active game');
                return;
            }

            guessBtn.disabled = true;
            guessInput.disabled = true;

            fetch('/api/games/' + currentGameId + '/guess?word=' + word, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    addAttemptToBoard(data.guessedWord, data.feedback, data.attempt);
                    document.getElementById('currentAttempts').textContent = data.currentAttempts;
                    guessInput.value = '';

                    if (data.isCorrect) {
                        showGameResult(true, `Congratulations! You guessed the word: ${data.guessedWord}`);
                    } else if (data.currentAttempts >= data.maxAttempts) {
                        showGameResult(false, `Game Over! The word was: (hidden)`);
                    } else {
                        guessInput.focus();
                    }

                    guessBtn.disabled = false;
                    guessInput.disabled = false;
                } else {
                    showError(data.message);
                    guessBtn.disabled = false;
                    guessInput.disabled = false;
                }
            })
            .catch(error => {
                showError('Error making guess: ' + error.message);
                guessBtn.disabled = false;
                guessInput.disabled = false;
            });
        }

        function addAttemptToBoard(word, feedback, attemptNumber) {
            const attemptsContainer = document.getElementById('attemptsContainer');
            
            const attemptDiv = document.createElement('div');
            attemptDiv.className = 'flex justify-between items-center bg-gray-100 p-4 rounded-lg animate-slide-up';
            
            const wordSpan = document.createElement('span');
            wordSpan.className = 'font-bold text-xl tracking-widest text-gray-800';
            wordSpan.textContent = word;
            
            const feedbackDiv = document.createElement('div');
            feedbackDiv.className = 'flex gap-2';
            
            for (let i = 0; i < feedback.length; i++) {
                const letterDiv = document.createElement('span');
                letterDiv.className = 'inline-flex items-center justify-center w-8 h-8 rounded font-bold text-white text-sm';
                
                if (feedback[i] === '2') {
                    letterDiv.className += ' bg-green-500';
                    letterDiv.textContent = '✓';
                } else if (feedback[i] === '1') {
                    letterDiv.className += ' bg-yellow-400 text-gray-800';
                    letterDiv.textContent = '→';
                } else {
                    letterDiv.className += ' bg-gray-500';
                    letterDiv.textContent = '✗';
                }
                
                feedbackDiv.appendChild(letterDiv);
            }
            
            attemptDiv.appendChild(wordSpan);
            attemptDiv.appendChild(feedbackDiv);
            attemptsContainer.appendChild(attemptDiv);
            attemptsContainer.scrollTop = attemptsContainer.scrollHeight;
        }

        function showGameResult(won, message) {
            const gameResult = document.getElementById('gameResult');
            const resultTitle = document.getElementById('resultTitle');
            const resultMessage = document.getElementById('resultMessage');
            
            gameResult.classList.remove('hidden', 'bg-red-100', 'border-red-500', 'text-red-700', 'bg-green-100', 'border-green-500', 'text-green-700');
            
            if (won) {
                gameResult.classList.add('bg-green-100', 'border-l-4', 'border-green-500', 'text-green-700');
                resultTitle.textContent = '🎉 YOU WON!';
            } else {
                gameResult.classList.add('bg-red-100', 'border-l-4', 'border-red-500', 'text-red-700');
                resultTitle.textContent = '😢 GAME OVER';
            }
            
            resultMessage.textContent = message;
            gameResult.classList.remove('hidden');
            
            document.getElementById('inputSection').classList.add('hidden');
            document.getElementById('newGameBtn').classList.remove('hidden');
        }

        function resetGame() {
            currentGameId = null;
            document.getElementById('gameBoard').classList.add('hidden');
            document.getElementById('themeSelector').classList.remove('hidden');
            document.getElementById('gameResult').classList.add('hidden');
            document.getElementById('attemptsContainer').innerHTML = '';
            guessInput.value = '';
            startGameBtn.disabled = false;
            themeSelect.value = '';
        }

        function showError(message) {
            const errorMessage = document.getElementById('errorMessage');
            errorMessage.textContent = message;
            errorMessage.classList.remove('hidden');
            setTimeout(() => {
                errorMessage.classList.add('hidden');
            }, 5000);
        }

        function showSuccess(message) {
            const successMessage = document.getElementById('successMessage');
            successMessage.textContent = message;
            successMessage.classList.remove('hidden');
            setTimeout(() => {
                successMessage.classList.add('hidden');
            }, 5000);
        }

        function showLoading(show) {
            const loading = document.getElementById('loadingSpinner');
            if (show) {
                loading.classList.remove('hidden');
            } else {
                loading.classList.add('hidden');
            }
        }

        window.addEventListener('load', function() {
            themeSelect.focus();
        });
    </script>
</body>
</html>