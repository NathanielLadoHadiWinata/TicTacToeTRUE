import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundEffect {
    private Clip backgroundClip;
    private Clip victoryClip;
    private Clip playerWinClip;
    private Clip aiWinClip;
    private boolean soundsLoaded = false;

    public SoundEffect() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            // Get the absolute path of the sounds directory
            String soundsPath = new File("sounds").getAbsolutePath();

            // Load each sound file
            File bgMusic = new File(soundsPath + File.separator + "bg-music.wav");
            File pvpWinMusic = new File(soundsPath + File.separator + "Win.wav");
            File playerWinMusic = new File(soundsPath + File.separator + "Win.wav");
            File aiWinMusic = new File(soundsPath + File.separator + "you-lose.wav");

            // For now, if specific files don't exist, use Win.wav as fallback
            File defaultMusic = new File(soundsPath + File.separator + "bg-music.wav");

            // Load background music
            if (bgMusic.exists()) {
                AudioInputStream bgStream = AudioSystem.getAudioInputStream(bgMusic);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(bgStream);
            } else if (defaultMusic.exists()) {
                AudioInputStream bgStream = AudioSystem.getAudioInputStream(defaultMusic);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(bgStream);
            }

            // Load PVP victory music
            if (pvpWinMusic.exists()) {
                AudioInputStream victoryStream = AudioSystem.getAudioInputStream(pvpWinMusic);
                victoryClip = AudioSystem.getClip();
                victoryClip.open(victoryStream);
            } else if (defaultMusic.exists()) {
                AudioInputStream victoryStream = AudioSystem.getAudioInputStream(defaultMusic);
                victoryClip = AudioSystem.getClip();
                victoryClip.open(victoryStream);
            }

            // Load player win music (vs AI)
            if (playerWinMusic.exists()) {
                AudioInputStream playerWinStream = AudioSystem.getAudioInputStream(playerWinMusic);
                playerWinClip = AudioSystem.getClip();
                playerWinClip.open(playerWinStream);
            } else if (defaultMusic.exists()) {
                AudioInputStream playerWinStream = AudioSystem.getAudioInputStream(defaultMusic);
                playerWinClip = AudioSystem.getClip();
                playerWinClip.open(playerWinStream);
            }

            // Load AI win music
            if (aiWinMusic.exists()) {
                AudioInputStream aiWinStream = AudioSystem.getAudioInputStream(aiWinMusic);
                aiWinClip = AudioSystem.getClip();
                aiWinClip.open(aiWinStream);
            } else if (defaultMusic.exists()) {
                AudioInputStream aiWinStream = AudioSystem.getAudioInputStream(defaultMusic);
                aiWinClip = AudioSystem.getClip();
                aiWinClip.open(aiWinStream);
            }

            soundsLoaded = true;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error loading sound files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playBackgroundMusic() {
        if (soundsLoaded && backgroundClip != null) {
            backgroundClip.setMicrosecondPosition(0);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (soundsLoaded && backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    public void playVictoryMusic() {
        stopBackgroundMusic();
        if (soundsLoaded && victoryClip != null) {
            victoryClip.setMicrosecondPosition(0);
            victoryClip.start();
        }
    }

    public void playPlayerWinMusic() {
        stopBackgroundMusic();
        if (soundsLoaded && playerWinClip != null) {
            playerWinClip.setMicrosecondPosition(0);
            playerWinClip.start();
        }
    }

    public void playAIWinMusic() {
        stopBackgroundMusic();
        if (soundsLoaded && aiWinClip != null) {
            aiWinClip.setMicrosecondPosition(0);
            aiWinClip.start();
        }
    }

    public void stopAllSounds() {
        if (!soundsLoaded) return;

        if (backgroundClip != null) backgroundClip.stop();
        if (victoryClip != null) victoryClip.stop();
        if (playerWinClip != null) playerWinClip.stop();
        if (aiWinClip != null) aiWinClip.stop();
    }
}