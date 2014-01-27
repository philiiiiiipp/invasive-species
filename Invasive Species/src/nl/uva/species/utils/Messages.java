package nl.uva.species.utils;

public enum Messages {
    
    // this is for our own experiment (ourExperiment.py)
    LEARN_COST_PARAM("learn cost parameters", "message understood, learning cost parameters now"),
    LEARN_MODEL("learn model", "message understood, learning model now"),
    PLAN("plan", "message understood, planning now"),
    EVALUATE("evaluate", "message understood, evaluating agent now"),
    FOLLOW_HEURISTICS("follow heuristics", "message understood, following heuristics now"),
    
    // this is for their experiment (invasiveExperiment.py)
	FREEZE_LEARNING("freeze learning", "message understood, policy frozen"),
	UNFREEZE_LEARNING("unfreeze learning", "message understood, policy unfrozen"),
	FREEZE_EXPLORING("freeze exploring", "message understood, exploring frozen"),
	UNFREEZE_EXPLORING("unfreeze exploring", "message understood, exploring frozen"),
	ERROR("", "Invasive agent does not understand your message.");

	private final String mIncomingMessage;

	private final String mOutgoingMessage;

	Messages(final String incoming, final String outgoing) {
		mIncomingMessage = incoming;
		mOutgoingMessage = outgoing;
	}

	/**
	 * Get the incoming message
	 * 
	 * @return the incoming message
	 */
	public String incomingMessage() {
		return mIncomingMessage;
	}

	/**
	 * Get the outgoing message
	 * 
	 * @return the outgoing message
	 */
	public String outgoingMessage() {
		return mOutgoingMessage;
	}
}