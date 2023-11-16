/*
* Sprawdzenie dostępności zasobu / obecności wolnego miejsca w buforze wymaga użycia instrukcji `while`
* z powodu sposobu, w jaki działa `notify` - programista nie ma bezpośredniej kontroli nad tym, który wątek zostanie aktywowany.
* W przypadku działania wielu wątków tej samej klasy (Producer/Consumer),
* obudzenie pewnego z nich nie gwarantuje, że warunek, na którym został zawieszony, już zachodzi.
*
* Przykład:
*   Wątek Producer1 zapisuje wartość do buforu i budzi wątek Producer2.
*   W przypadku zastosowania instrukcji `if` Producer2 nadpisze zawartość buforu.
* */

public class Buffer {
    private String content = null;
    private boolean isEmpty = true;

    public synchronized void put(String message) throws InterruptedException {
        while (!isEmpty)
            wait();

        content = message;
        isEmpty = false;

        notifyAll();
    }

    public synchronized String take() throws InterruptedException {
            while (isEmpty)
                wait();

            isEmpty = true;
            notifyAll();

            return content;
    }
}
