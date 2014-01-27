package nl.uva.species.utils;

public enum Messages {
    
    // this is for our own experiment (ourExperiment.py)
    LEARN_COST_PARAM("learn cost parameters", "message understood, policy frozen"),
    LEARN_MODEL("learn model", "message understood, policy frozen"),
    PLAN("plan", "message understood, policy frozen"),
    EVALUATE("evaluate", "message understood, policy frozen"),
    
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