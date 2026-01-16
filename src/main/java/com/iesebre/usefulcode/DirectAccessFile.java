package com.iesebre.usefulcode;

import java.io.*;

/**
 * 1. Què és DirectAccessFile?
 * DirectAccessFile{@literal <}T{@literal >} és una classe genèrica que permet guardar, llegir, inserir, actualitzar i esborrar objectes dins d’un fitxer binari utilitzant accés directe (RandomAccessFile).
 * Característiques principals:
 * Permet guardar objectes de qualsevol classe sempre que implementin Serializable.
 *
 *
 * Els objectes es poden:
 *
 *
 * Accedir per posició (accés directe).
 *
 *
 * Recórrer seqüencialment.
 *
 *
 * Es poden fer:
 *
 *
 * Insercions en qualsevol posició.
 *
 *
 * Actualitzacions.
 *
 *
 * Esborrats.
 *
 *
 *
 * 2. Requisits per als objectes
 * Qualsevol classe que es vulgui guardar ha d’implementar Serializable.
 * Exemple:
 * import java.io.Serializable;
 *
 * public class Alumne implements Serializable {
 *     private String nom;
 *     private int edat;
 *
 *     public Alumne(String nom, int edat) {
 *         this.nom = nom;
 *         this.edat = edat;
 *     }
 *
 *     &#64;Override
 *     public String toString() {
 *         return nom + " (" + edat + ")";
 *     }
 * }
 *
 *
 * 3. Crear un fitxer d’accés directe
 * Amb nom de fitxer personalitzat
 * DirectAccessFile{@literal <}Alumne{@literal >} daf = new DirectAccessFile{@literal <}{@literal >}("alumnes.dat");
 *
 * Amb nom per defecte (dades.dat)
 * DirectAccessFile{@literal <}Alumne{@literal >} daf = new DirectAccessFile{@literal <}{@literal >}();
 *
 * 💡 Es recomana utilitzar try-with-resources perquè el fitxer es tanqui automàticament:
 * try (DirectAccessFile{@literal <}Alumne{@literal >} daf = new DirectAccessFile{@literal <}{@literal >}("alumnes.dat")) {
 *     // treball amb el fitxer
 * }
 *
 *
 * 4. Escriure objectes
 * Afegir un objecte al final del fitxer
 * daf.writeObject(new Alumne("Anna", 20));
 * daf.writeObject(new Alumne("Marc", 22));
 *
 * Inserir un objecte en una posició concreta
 * daf.writeObject(new Alumne("Laura", 21), 1);
 *
 * 📌 Les posicions comencen a 0
 * Posició 0 → primer objecte
 *
 *
 * Posició 1 → segon objecte
 *
 *
 * Si la posició és més gran que la mida actual, l’objecte s’afegeix al final.
 *
 * 5. Llegir objectes
 * Llegir un objecte per posició
 * Alumne a = daf.readObject(0);
 * System.out.println(a);
 *
 * Llegir objectes seqüencialment
 * daf.goToBeginning();
 * Alumne a;
 *
 * while ((a = daf.readObject()) != null) {
 *     System.out.println(a);
 * }
 *
 *
 * 6. Saber quants objectes hi ha
 * int total = daf.size();
 * System.out.println("Nombre d'objectes: " + total);
 *
 *
 * 7. Esborrar objectes
 * Esborrar un objecte per posició
 * Alumne eliminat = daf.deleteObject(1);
 * System.out.println("Eliminat: " + eliminat);
 *
 * L’objecte es retorna abans de ser esborrat (si existeix).
 *
 * 8. Actualitzar objectes
 * Alumne anterior = daf.updateObject(new Alumne("Marc", 23), 1);
 * System.out.println("Abans de l'actualització: " + anterior);
 *
 * 📌 Internament:
 * Es llegeix l’objecte antic.
 *
 *
 * S’esborra.
 *
 *
 * S’insereix el nou a la mateixa posició.
 *
 *
 *
 * 9. Esborrar tot el contingut del fitxer
 * daf.deleteAll();
 *
 * El fitxer queda buit i el nombre d’objectes passa a ser 0.
 *
 * 10. Navegació pel fitxer
 * daf.goToBeginning(); // Anar a l'inici del fitxer
 * daf.goToEnd();       // Anar al final del fitxer
 *
 * Això és útil sobretot per a lectures seqüencials.
 *
 * 11. Resum de mètodes principals
 * Mètode
 * Funció
 * writeObject(obj)
 * Afegeix un objecte al final
 * writeObject(obj, pos)
 * Insereix un objecte a una posició
 * readObject(pos)
 * Llegeix un objecte per posició
 * readObject()
 * Llegeix seqüencialment
 * deleteObject(pos)
 * Esborra un objecte
 * updateObject(obj, pos)
 * Actualitza un objecte
 * size()
 * Nombre d’objectes
 * deleteAll()
 * Esborra tot el fitxer
 *
 *
 * 12. Quan és útil aquesta classe?
 * Quan es vol treballar amb fitxers d’objectes sense usar bases de dades.
 *
 *
 * Quan cal accés directe per índex.
 *
 *
 */

public class DirectAccessFile<T extends Serializable> implements Closeable, AutoCloseable {

    // Properties
    private String name = "dades.dat";
    private RandomAccessFile raf;
    private int comptObjs = 0;

    // Constructors

    /**
     * Creates a direct access file with the specified name
     *
     * @param name the name given to the file
     * @throws FileNotFoundException if an error occurs with the file name that prevents its creation
     */
    public DirectAccessFile(String name) throws IOException {
        this.name = name;
        raf = new RandomAccessFile(name, "rw");
        comptObjs=countObjects();
    }

    //When creating the daf we need to know how many objects does it have
    private int countObjects() throws IOException {
        int count=0;
        this.goToBeginning();
        for(;readObjectInit()!=null;count++);
        return count;
    }

    /**
     * Creates a direct access file with the default name
     *
     * @throws FileNotFoundException if an error occurs with the file name that prevents its creation
     */
    public DirectAccessFile() throws IOException {
        raf = new RandomAccessFile(name, "rw");
        comptObjs=countObjects();
    }

    // Getters and setters

    /**
     * Shows the relative name of the file
     *
     * @return the relative name of the file
     */
    public String getName() {
        return name;
    }

    // Class methods

    /**
     * Writes a new object at the specified position in the file
     *
     * @param object   instance of class T to be saved in the file
     * @param position an integer value
     * @return true if the object was successfully written, false otherwise. The position must be greater than or equal to 0.
     * @throws IOException            if an input/output error occurs
     */
    public boolean writeObject(T object, int position) throws IOException {
        if (position < 0) return false;

        // Find the position where the new object should go, skipping previous objects until reaching the position or the end of the file (in this case, the position will be the last one, and may not match the specified one)
        if (position < this.size()) {
            // Process the object to be inserted
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            byte[] data = bos.toByteArray();

            // Find the insertion position
            int compt = 1;
            long punter = 0;
            do {
                raf.seek(punter);
                int tamany = raf.readInt(); // Read the length of the object

                if (position + 1 == compt) {  // we are at the desired object
                    moveForward(data.length + 8, punter);
                    break;
                }
                compt++;
                punter += tamany + 8;
            } while (true);

            raf.seek(punter);
            raf.writeInt(data.length); // Write the length of the object
            raf.write(data); // Write the object data
            raf.writeInt(data.length); // Write the length of the object
            // Increment the object counter of the file
            comptObjs++;

        } else this.writeObject(object);

        return true;
    }

    private void moveForward(int leaveFree, long initialPosition) throws IOException {
        // Move the file content to leave free space
        long fileLength = raf.length();

        long readPos = fileLength - 4; // We will read the size of the last object by placing readPos at its final block

        while (readPos > initialPosition) {
            raf.seek(readPos); // where we store its size (this 4 is correct)
            int tamany = raf.readInt();
            readPos = readPos - tamany - 4; // Place readPos at the beginning of the last object
            long writePos = readPos + leaveFree;
            raf.seek(readPos);
            byte[] buffer = new byte[tamany + 8]; // number of bytes to read, the size of an object including sizes
            int bytesRead = raf.read(buffer);
            raf.seek(writePos);
            raf.write(buffer, 0, bytesRead);
            readPos -= 4; // place at the end of the previous object
        }
    }

    /**
     * Writes a new object at the end of the file
     *
     * @param object instance of class T to be saved in the file
     * @throws IOException if an input/output error occurs
     */
    public void writeObject(T object) throws IOException {
        // Place at the end of the file
        raf.seek(raf.length());

        // Write the object
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        byte[] data = bos.toByteArray();
        raf.writeInt(data.length); // Write the length of the object
        raf.write(data); // Write the object data
        raf.writeInt(data.length); // Write the length of the object
        comptObjs++; // Increment the object counter of the file
    }

    /**
     * Retrieves the instance at the specified position in the file
     *
     * @param position an integer value
     * @return the object read from the file, or null if the position is not greater than or equal to 0, or is nonexistent, or there are no objects
     * @throws IOException            if an input/output error occurs
     * @throws ClassNotFoundException if the class of the instance to be saved is not found
     */
    public T readObject(int position) throws IOException, ClassNotFoundException {
        // If there are no objects or the position is incorrect, return null
        if (position < 0 || comptObjs == 0 || position >= comptObjs) return null;

        // Find the position in the file of the object, skipping previous objects until reaching
        int compt = 1;
        long punter = 0;

        do {
            raf.seek(punter);
            int length = raf.readInt(); // Read the length of the object

            if (position + 1 == compt) {  // we are at the desired object
                byte[] data = new byte[length];
                raf.readFully(data); // Read the object data
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bis);
                return ((T) ois.readObject());
            }
            compt++;
            punter += length + 8; // 4 bytes for each size variable we put before and after the object
        } while (true);
    }


    /**
     * Retrieves the instance at the current position of the file pointer
     *
     * @return the object read from the file, or null if the current position of the pointer is incorrect or there are no objects
     * @throws IOException            if an input/output error occurs
     */
    public T readObject() throws IOException {
        // If there are no objects
        if (comptObjs == 0) return null;

        // Find the position in the file of the object, skipping previous objects until reaching
        try {
            int length = raf.readInt(); // Read the length of the object

            byte[] data = new byte[length];
            raf.readFully(data); // Read the object data
            raf.seek(raf.getFilePointer() + 4);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ((T) ois.readObject());
        } catch (ClassNotFoundException | EOFException e) {
            // If the position was incorrect
        }
        return null;
    }

    //Is like readObject() without taking into account the current value of comptObjs
    private T readObjectInit() throws IOException {

        // Find the position in the file of the object, skipping previous objects until reaching
        try {
            int length = raf.readInt(); // Read the length of the object

            byte[] data = new byte[length];
            raf.readFully(data); // Read the object data
            raf.seek(raf.getFilePointer() + 4);
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ((T) ois.readObject());
        } catch (ClassNotFoundException | EOFException e) {
            // If the position was incorrect
        }
        return null;
    }

    /**
     * Deletes all content of the file
     *
     * @throws IOException if an input/output error occurs
     */
    public void deleteAll() throws IOException {
        raf.setLength(0L);
        comptObjs = 0; // Set the object counter of the file to 0
    }

    /**
     * Returns the number of objects in the file
     *
     * @return an integer indicating the number of objects in the file
     */
    public int size() {
        return comptObjs;
    }

    /**
     * Moves to the beginning of the file
     * @throws IOException if an input/output error occurs
     */
    public void goToBeginning() throws IOException {
        raf.seek(0);
    }

    /**
     * Moves to the end of the file
     * @throws IOException if an input/output error occurs
     */
    public void goToEnd() throws IOException {
        raf.seek(raf.length());
    }

    /**
     * Deletes the object at the specified position in the file
     *
     * @param position an integer value
     * @return the deleted object if found, null otherwise. The position must be greater than or equal to 0 and less than the number of objects in the file.
     * @throws IOException            if an input/output error occurs
     * @throws ClassNotFoundException if the class of the instance to be deleted is not found
     */
    public T deleteObject(int position) throws IOException, ClassNotFoundException {
        if (position < 0 || comptObjs == 0 || position >= comptObjs) return null;

        // Retrieve the object to be deleted
        T resultat = (T) this.readObject(position); // We found an object

        // Find the deletion position
        int compt = 1;
        long punter = 0;
        do {
            raf.seek(punter);
            int tamany = raf.readInt(); // Read the length of the object

            if (position + 1 == compt) { // we are at the desired object
                moveBackwards(tamany + 8, punter);
                break;
            }
            compt++;
            punter += tamany + 8;
        } while (true);

        // Decrement the object counter of the file
        comptObjs--;

        return resultat;
    }

    private void moveBackwards(int movementSize, long initialPosition) throws IOException {
        // Move the file content to leave free space
        long fileLength = raf.length();
        long readPos = initialPosition + movementSize;
        long writePos = initialPosition;

        while (readPos < fileLength) {
            raf.seek(readPos);
            int length = raf.readInt(); // Read the length of the object
            byte[] buffer = new byte[length + 8];
            raf.seek(readPos);
            int bytesRead = raf.read(buffer);
            raf.seek(writePos);
            raf.write(buffer, 0, bytesRead);
            readPos += bytesRead;
            writePos += bytesRead;
        }

        //Acursem el fitxer, sinó se queden objectes pel final
        raf.setLength(raf.length()-movementSize);
    }

    /**
     * Updates the object at the specified position in the file
     *
     * @param object the new props of the updated object
     * @param position an integer value
     * @return the updated object before update if found, null otherwise. The position must be greater than or equal to 0 and less than the number of objects in the file.
     * @throws IOException            if an input/output error occurs
     * @throws ClassNotFoundException if the class of the instance to be updated is not found
     */
    public T updateObject(T object, int position) throws IOException, ClassNotFoundException {
        if (position < 0 || comptObjs == 0 || position >= comptObjs) return null;

        // Retrieve the object to be updated
        T resultat = (T) this.readObject(position); // We found an object

        // First we delete the current object and then we insert the new
        this.deleteObject(position);
        this.writeObject(object, position);

        return resultat;
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     *
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     *
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     *
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws IOException if this resource cannot be closed
     */
    @Override
    public void close() throws IOException {
        raf.close();
    }

}
