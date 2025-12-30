package Main.Java.Notes;

public class NoteEvent {

    public long time; //tempo em milisegundos
    public int[] lanes; //1 ou mais trilhas para acertar as notas

    //Construtor
    public NoteEvent(long time, int... lanes){
        this.time = time;
        this.lanes = lanes;
    }
}
