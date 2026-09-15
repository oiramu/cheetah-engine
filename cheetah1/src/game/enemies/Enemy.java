/*
 * Copyright 2026 Carlos Rodriguez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package game.enemies;

import engine.components.GameComponent;
import engine.core.Transform;

/**
 * Shared shape genuinely identical across all 8 enemy types: the
 * transform, the core FSM bookkeeping fields, and the "wake up + take
 * damage" arithmetic every damage() method starts with.
 *
 * Deliberately NOT a full state-machine template - the 8 enemies'
 * update() bodies differ too structurally for that (different state
 * sets, Zombie's dual-personality seed system, Commander's extra
 * rocket state, Ghost's self-removal instead of a lootable corpse).
 * Each subclass keeps its own update()/render()/damage() bodies,
 * just built on these shared fields instead of private duplicates.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public abstract class Enemy extends GameComponent {

	protected Transform transform;

	protected int state;
	protected boolean canAttack;
	protected boolean canLook;
	protected boolean dead;
	protected double deathTime;
	protected double health;

	/**
	 * Gets the enemy's actual transformation.
	 * @return the enemy's transform data.
	 */
	public Transform getTransform() {return transform;}

	/**
	 * Gets if the enemy is dead or not.
	 * @return the enemy's life state.
	 */
	public boolean isAlive() {return !dead;}

	/**
	 * Gets the enemy's actual health.
	 * @return enemy's health.
	 */
	public double getHealth() {return health;}

	/**
	 * Marks this enemy dead and settles it straight into its corpse state -
	 * used only to restore a save's state onto a freshly-regenerated level
	 * (where every enemy starts alive again), never during normal combat.
	 * Each subclass sets its own state to its own STATE_DEAD constant (the
	 * 8 enemies don't share one dying/dead state machine - see the class
	 * comment above), which is what its own update() already uses to pick
	 * the corpse sprite and stop running its AI switch.
	 */
	public abstract void killInstantly();

	/**
	 * The "wake from idle, subtract health" half of damage() that's
	 * identical across every enemy - the STATE_IDLE/STATE_CHASE values
	 * are passed in since every enemy declares its own (differently
	 * valued) state constants.
	 * @param amt amount of damage.
	 * @param idleState this enemy's STATE_IDLE value.
	 * @param chaseState this enemy's STATE_CHASE value.
	 */
	protected void wakeAndDamage(int amt, int idleState, int chaseState) {
		if (state == idleState)
			state = chaseState;

		health -= amt;
	}

	/**
	 * The condition every damage() method uses to decide whether to
	 * flinch/play a hit sound: still alive, and this was a real hit
	 * (not e.g. a status-effect tick with amt <= 0).
	 * @param amt amount of damage just applied.
	 * @return hit-reaction state.
	 */
	protected boolean tookNonlethalHit(int amt) {
		return health > 0 && amt > 0;
	}

}
