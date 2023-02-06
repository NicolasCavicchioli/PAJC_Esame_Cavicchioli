package pajc.event;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.HashMap;
import function.MultiConsumer;
import pajc.PAJC;

public class MyEventHandler<Event extends IEvent> {	
	
	HashMap<Event,ArrayList<MultiConsumer>> map = new HashMap<>();
	
	
	public void registerAction(Event event, MultiConsumer consumer) {
		requireNonNull(event); requireNonNull(consumer);
		var list = PAJC.getOrPutDefault(map, event, ArrayList::new);
		list.add(consumer);
	}
	
	public void trigger(Event event, Object...arg) {
		requireNonNull(event);
		
		var list = map.get(event);
		if (isNull(list)) return;
		
		list.forEach(action -> action.accept(arg));
		
	}
	
}
