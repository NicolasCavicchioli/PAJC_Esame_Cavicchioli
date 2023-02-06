package pajc.event;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface KeyListener extends java.awt.event.KeyListener {
	
	default void keyPressed (KeyEvent e) {}
	default void keyTyped	(KeyEvent e) {}
	default void keyReleased(KeyEvent e) {}

	static KeyListener whenKeyPressed(Consumer<KeyEvent> consumer) {
		return new KeyListener(){
			public void keyPressed(KeyEvent e){
				consumer.accept(e);
			}
		};
	}
	static KeyListener whenKeyPressed(IntConsumer consumer) {
		return whenKeyPressed((KeyEvent e) -> consumer.accept(e.getKeyCode()));
	}
	
}
