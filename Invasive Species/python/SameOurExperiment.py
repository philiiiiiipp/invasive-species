# Experiment for
# --- TRAINING ---
#  - learning the cost parameters
#  - learning the model (genetic algorithm)
#  - planning with the learned model (sparse cooperative Q-learning)
# --- EVALUATION ---
#  - evaluating the policy

from numpy.numarray import array
import random
import math
import rlglue.RLGlue as RLGlue

# -------- initialize --------

RLGlue.RL_init()
nbrReaches=7
habitatSize=4
random.seed(5)
S = array([random.randint(1, 3) for i in xrange(nbrReaches * habitatSize)])
S = ",".join(map(str, S))
RLGlue.RL_env_message("set-start-state "+S)

# -------- open files --------

f = open('experiments.txt','a')
f.write("\n")

f.write("--> budget = 20 \n")

# -------- learn cost parameters --------
# need: 100 episodes
#RLGlue.RL_agent_message("learn cost parameters")
#for i in range(0,1000):
#   RLGlue.RL_episode(100)

# -------- learn model with genetic algorithm --------

RLGlue.RL_agent_message("learn model")
RLGlue.RL_episode(3000)

# -------- plan on the learned model --------

RLGlue.RL_agent_message("plan")

# -------- evaluate policy --------

RLGlue.RL_agent_message("evaluate")
print "Evaluating the AA policy"
n = 10
sum = 0
sum_of_squares = 0
random.seed(5)
for i in range(0, n):
        S = array([random.randint(1, 3) for i in xrange(nbrReaches * habitatSize)])
        S = ",".join(map(str, S))
        RLGlue.RL_env_message("set-start-state "+S)
        RLGlue.RL_episode(100)
        this_return = RLGlue.RL_return()
        print this_return
        sum += this_return
        sum_of_squares += this_return ** 2
        print "The return after 100 episodes is: "+str(this_return)
mean = sum / n
variance = (sum_of_squares - n * mean * mean) / (n - 1.0)
std = math.sqrt(variance)

f.write("    INVASIVE AGENT  :   mean: ")
f.write(str(mean))
f.write(" , std: ")
f.write(str(std))
f.write("\n")

# -------- evaluate simple heuristic agent --------

RLGlue.RL_agent_message("follow heuristics")
print "Evaluating the simple heuristic agent"
n = 10
sum = 0
sum_of_squares = 0
random.seed(5)
for i in range(0, 10):
        S = array([random.randint(1, 3) for i in xrange(nbrReaches * habitatSize)])
        S = ",".join(map(str, S))
        RLGlue.RL_env_message("set-start-state "+S)
        RLGlue.RL_episode(100)
        this_return = RLGlue.RL_return()
        print this_return
        sum += this_return
        sum_of_squares += this_return ** 2
        print "The return after 100 episodes is: "+str(this_return)
mean = sum / n
variance = (sum_of_squares - n * mean * mean) / (n - 1.0)
std = math.sqrt(variance)

f.write("    HEURISTIC AGENT :   mean: ")
f.write(str(mean))
f.write(" , std: ")
f.write(str(std))
f.write("\n")
        
# -------- end --------

f.close()
RLGlue.RL_cleanup()
print "Program complete."
