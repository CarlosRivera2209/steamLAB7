package steam_lab7;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Steam {

    private RandomAccessFile codesFile;
    private RandomAccessFile gamesFile;
    private RandomAccessFile playersFile;
    
    private final String folderPath = "steam";
    private final String downloadsFolderPath = folderPath + File.separator + "downloads";

    public Steam() throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File downloadsFolder = new File(downloadsFolderPath);
        if (!downloadsFolder.exists()) {
            downloadsFolder.mkdirs();
        }

        codesFile = new RandomAccessFile(folderPath + File.separator + "codes.stm", "rw");
        gamesFile = new RandomAccessFile(folderPath + File.separator + "games.stm", "rw");
        playersFile = new RandomAccessFile(folderPath + File.separator + "players.stm", "rw");

        if (codesFile.length() == 0) {
            codesFile.writeInt(1);
            codesFile.writeInt(1);
            codesFile.writeInt(1);
        }
    }

    public int getNextGameCode() throws IOException {
        int nextGameCode = codesFile.readInt();
        codesFile.seek(0);
        codesFile.writeInt(nextGameCode + 1);
        return nextGameCode;
    }

    public int getNextPlayerCode() throws IOException {
        int nextPlayerCode = codesFile.readInt();
        codesFile.seek(4);
        codesFile.writeInt(nextPlayerCode + 1);
        return nextPlayerCode;
    }

    public int getNextDownloadCode() throws IOException {
        int nextDownloadCode = codesFile.readInt();
        codesFile.seek(8);
        codesFile.writeInt(nextDownloadCode + 1);
        return nextDownloadCode;
    }

    public void addGame(int code, String titulo, char sistemaOperativo, int edadMinima, double precio, byte[] foto) throws IOException {
        gamesFile.seek(gamesFile.length());
        gamesFile.writeInt(code);
        gamesFile.writeUTF(titulo);
        gamesFile.writeChar(sistemaOperativo);
        gamesFile.writeInt(edadMinima);
        gamesFile.writeDouble(precio);
        gamesFile.writeInt(0);
        gamesFile.writeInt(foto.length);
        gamesFile.write(foto);
    }

    public void addPlayer(int code, String username, String password, String nombre, Calendar nacimiento, byte[] foto, String tipoUsuario) throws IOException {
        playersFile.seek(playersFile.length());
        playersFile.writeInt(code);
        playersFile.writeUTF(username);
        playersFile.writeUTF(password);
        playersFile.writeUTF(nombre);
        playersFile.writeLong(nacimiento.getTimeInMillis());
        playersFile.writeInt(0);
        playersFile.writeInt(foto.length);
        playersFile.write(foto);
        playersFile.writeUTF(tipoUsuario);
    }

    public boolean downloadGame(int gameCode, int playerCode, char sistemaOperativo, Calendar fechaDescarga) throws IOException {
        gamesFile.seek(0);
        while (gamesFile.getFilePointer() < gamesFile.length()) {
            int code = gamesFile.readInt();
            String titulo = gamesFile.readUTF();
            char so = gamesFile.readChar();
            int edadMinima = gamesFile.readInt();
            double precio = gamesFile.readDouble();
            int downloadCount = gamesFile.readInt();
            int fotoLength = gamesFile.readInt();
            byte[] foto = new byte[fotoLength];
            gamesFile.readFully(foto);

            if (code == gameCode && so == sistemaOperativo) {
                playersFile.seek(0);
                while (playersFile.getFilePointer() < playersFile.length()) {
                    int playerCodeFromFile = playersFile.readInt();
                    playersFile.readUTF();
                    playersFile.readUTF();
                    playersFile.readUTF();
                    playersFile.readLong();
                    int playerDownloadCount = playersFile.readInt();
                    playersFile.skipBytes(playersFile.readInt());
                    playersFile.readUTF();

                    if (playerCodeFromFile == playerCode) {
                        Calendar fechaNacimiento = Calendar.getInstance();
                        fechaNacimiento.setTimeInMillis(playersFile.readLong());
                        fechaNacimiento.add(Calendar.YEAR, edadMinima);
                        if (fechaNacimiento.compareTo(fechaDescarga) <= 0) {
                            File downloadFile = new File(downloadsFolderPath + File.separator + "download_" + getNextDownloadCode() + ".stm");
                            downloadFile.createNewFile();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String formattedDate = sdf.format(fechaDescarga.getTime());
                            String downloadInfo = String.format("[%s]%n%s%nDownload #%d%n%s has bajado %s a un precio de $%.2f",
                                    formattedDate, titulo, getNextDownloadCode(), "Nombre Cliente", titulo, precio);
                            gamesFile.seek(gamesFile.getFilePointer() - 4);
                            gamesFile.writeInt(downloadCount + 1);
                            playersFile.seek(playersFile.getFilePointer() - 4);
                            playersFile.writeInt(playerDownloadCount + 1);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        playersFile.skipBytes(5 + 8 + 4);
                    }
                }
            } else {
                gamesFile.skipBytes(2 + titulo.length() * 2 + 4 + 8 + 4 + fotoLength); // skip sistemaOperativo, titulo, edadMinima, precio, downloadCount, foto
            }
        }
        return false; 
    }

    public void updatePriceFor(int gameCode, double newPrice) throws IOException {
        long originalPosition = gamesFile.getFilePointer();
        gamesFile.seek(0);
        while (gamesFile.getFilePointer() < gamesFile.length()) {
            int code = gamesFile.readInt();
            if (code == gameCode) {
                gamesFile.readUTF();
                gamesFile.readChar();
                gamesFile.readInt();
                gamesFile.writeDouble(newPrice);
                gamesFile.seek(gamesFile.getFilePointer() - 8);
                gamesFile.writeDouble(newPrice);
                gamesFile.seek(originalPosition);
                return;
            } else {
                gamesFile.skipBytes(2 + gamesFile.readUTF().length() * 2 + 4 + 8 + 4);
            }
        }
        gamesFile.seek(originalPosition);
    }

    public void reportForClient(int clientCode, String txtFile) throws IOException {
        long originalPosition = playersFile.getFilePointer();
        playersFile.seek(0);
        while (playersFile.getFilePointer() < playersFile.length()) {
            int code = playersFile.readInt();
            if (code == clientCode) {
                String username = playersFile.readUTF();
                String password = playersFile.readUTF();
                String nombre = playersFile.readUTF();
                Calendar nacimiento = Calendar.getInstance();
                nacimiento.setTimeInMillis(playersFile.readLong());
                int downloadCount = playersFile.readInt();
                int fotoLength = playersFile.readInt();
                byte[] foto = new byte[fotoLength];
                playersFile.readFully(foto);
                String tipoUsuario = playersFile.readUTF();

                File reportFile = new File(txtFile);
                if (reportFile.createNewFile()) {
                    System.out.println("REPORTE CREADO");
                } else {
                    System.out.println("NO SE PUEDE CREAR REPORTE");
                }

                playersFile.seek(originalPosition);
                return;
            } else {
                playersFile.skipBytes(5 + playersFile.readUTF().length() * 2 + playersFile.readUTF().length() * 2 + 8 + 4 + playersFile.readInt() + 4 + playersFile.readUTF().length() * 2); // Skip username, password, nombre, nacimiento, downloadCount, foto, tipoUsuario
            }
        }
        playersFile.seek(originalPosition);
    }

    public void printGames() throws IOException {
        gamesFile.seek(0);
        while (gamesFile.getFilePointer() < gamesFile.length()) {
            int code = gamesFile.readInt();
            String titulo = gamesFile.readUTF();
            char sistemaOperativo = gamesFile.readChar();
            int edadMinima = gamesFile.readInt();
            double precio = gamesFile.readDouble();
            int downloadCount = gamesFile.readInt();
            int fotoLength = gamesFile.readInt();
            byte[] foto = new byte[fotoLength];
            gamesFile.readFully(foto);

            System.out.println("Code: " + code);
            System.out.println("Titulo: " + titulo);
            System.out.println("Sistema Operativo: " + sistemaOperativo);
            System.out.println("Edad Minima: " + edadMinima);
            System.out.println("Precio: " + precio);
            System.out.println("Download Count: " + downloadCount);
            // Print foto if needed
        }
    }
}
