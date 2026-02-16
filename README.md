🎵 SonicFlow

Un lecteur audio moderne et performant pour Android avec visualisation dynamique 🌊


✨ Fonctionnalités
🎧 Lecture Audio Avancée

▶️ Lecture en arrière-plan même téléphone verrouillé
🔔 Contrôles dans la notification (Précédent/Lecture/Suivant)
🎚️ Barre de progression interactive
🔄 Modes de lecture (Répétition, Aléatoire)

📚 Gestion de Bibliothèque

🎼 Scanner automatique des fichiers audio
🔍 Recherche rapide par titre, artiste ou album
🗂️ Tri personnalisable (A-Z, Date, Durée)
⭐ Gestion des favoris

🎨 Playlists Personnalisées

➕ Création de playlists manuelles
📝 Ajout/Suppression de morceaux
💾 Persistance locale avec Room Database
🔗 Relations Many-to-Many optimisées

🌊 Visualisation Waveform

📊 Génération dynamique de forme d'onde
🎨 Animation fluide avec Jetpack Compose Canvas
⚡ Calcul optimisé avec cache local
🎭 Effet visuel rotatif et pulsant


🏗️ Architecture Technique
🛠️ Technologies Utilisées
ComposantTechnologie🎨 UIJetpack Compose🏛️ ArchitectureMVVM + Clean Architecture💉 Injection de DépendancesHilt🗄️ Base de DonnéesRoom Database🎵 Lecteur AudioMedia3 (ExoPlayer)🔄 NavigationCompose Navigation⚡ AsynchroneKotlin Coroutines + Flow
📦 Structure du Projet
com.example.sonicflow/
├── 📱 data/
│   ├── model/          # Entités (Track, Playlist)
│   ├── database/       # Room DAO & Database
│   └── repository/     # Repositories
├── 🎯 domain/
│   └── usecase/        # Logique métier
├── 🎨 presentation/
│   ├── home/           # Écran principal
│   ├── player/         # Lecteur audio
│   ├── library/        # Bibliothèque
│   └── components/     # Composants réutilisables
└── 🔧 di/              # Modules Hilt

🚀 Installation
Prérequis

📱 Android Studio Hedgehog ou supérieur
🤖 SDK Android 24+ (Android 7.0)
☕ JDK 17

Étapes d'installation

Cloner le repository

bashgit git@github.com:Theophile706/sonicflow.git
cd sonicflow

Ouvrir dans Android Studio

bash# Ouvrir le projet avec Android Studio

Sync Gradle

bash# Cliquer sur "Sync Project with Gradle Files"

Lancer l'application

bash# Connecter un appareil Android ou lancer un émulateur
# Appuyer sur Run ▶️

🎯 Roadmap de Développement
✅ Semaine 1 : Architecture & Fichiers

 Setup projet (Hilt, Navigation, Room)
 Scanner MediaStore pour fichiers audio
 UI Bibliothèque avec tri et filtrage

✅ Semaine 2 : Lecture Audio

 MediaSessionService (Media3)
 Notification avec contrôles
 UI Lecteur avec SeekBar

✅ Semaine 3 : Playlists

 Base de données Room pour playlists
 UI gestion playlists
 Persistance état de lecture

✅ Semaine 4 : Waveform & Polish

 Génération waveform avec MediaExtractor
 Visualisation Canvas animée
 Tests finaux et corrections


📸 Captures d'écran
🏠 Écran Principal
Interface moderne avec liste des morceaux et recherche intégrée.
🎵 Lecteur Audio
Visualisation waveform circulaire avec contrôles fluides.
📚 Bibliothèque
Organisation par artistes, albums et playlists.

🎨 Thème & Design
🌈 Palette de Couleurs
CouleurHexUsage🟠 Orange Fluo#FF6600Accent principal⚫ Noir#000000Background⚪ Blanc#FFFFFFTexte principal🟢 Vert Clair#00FF00Waveform🔴 Rouge#FF4444Favoris

🔧 Configuration
Permissions Requises
xml<!-- Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Android 12 et inférieur -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

<!-- Service foreground -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />


📝 License
Ce projet est sous licence MIT - voir le fichier LICENSE pour plus de détails.

👨‍💻 Auteur RASOLOFOSON Iavotriniaina Theophile


📧 Email: rasolofosontheophile43@gmail.com
          theophilerasolofoson@gamil.com


🙏 Remerciements

🎵 Media3 - Framework audio Android
🎨 Jetpack Compose - UI moderne
💉 Hilt - Injection de dépendances
📚 Room - Persistence locale


<div align="center">
🌟 Si vous aimez ce projet, n'hésitez pas à mettre une étoile ! ⭐
Fait avec ❤️ et Kotlin
</div>
