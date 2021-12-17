import { NativeModules, NativeEventEmitter } from 'react-native';
import RNShake from 'react-native-shake';
const { RNWalkCounter } = NativeModules;
const WalkEvent = new NativeEventEmitter({ RNWalkCounter });
let subscription;
let subscription2;

/**
 * Starts counting the number of steps from 0.
 * @param {Object} config A JavaScript Object.
 * @param {Number} config.default_threshold Optional | Defaults to 15.0, More value of threshold means less sensitive counter.
 * @param {Number} config.default_delay Optional | Defaults to 150000000, It is the time delay between steps in nanoseconds.
 * @param {Number} config.cheatInterval Optional | Defaults to 3000, It is the time interval in milliseconds after which
 * the counter will 
 * again start counting steps, after a cheat event(Triggers when the user tries to simulate walking by some other activities 
 * such as shaking the device)
 * @param {Function} config.onStepCountChange A function which will be triggered when the count of steps changes.
 * @param {Function} config.onCheat Optional | A function which will be triggered when the user tries to cheat.
 */
function startCounter(config) {
    const default_threshold = config.default_threshold || 15.0;
    const default_delay = config.default_delay || 150000000;
    const cheatInterval = config.cheatInterval || 3000;
    const onStepCountChange = config.onStepCountChange;
    const onCheat = config.onCheat;
    let prevSteps = 0, currSteps = 0, currTime = 0;
    
    subscription = WalkEvent.addListener('onStepRunning', (event) => {
        if (currTime + cheatInterval < new Date().getTime()) {
            currSteps = Number(event.steps);
            if (onStepCountChange) {
                onStepCountChange(currSteps + prevSteps);
            }
        }
    });

    subscription2 = RNShake.addListener(() => {
        if (currTime + cheatInterval < new Date().getTime()) {
            RNWalkCounter.stopCounter();
            prevSteps = prevSteps + currSteps;
            currSteps = 0;
            currTime = new Date().getTime();
            if (onCheat) {
                onCheat();
            }
            setTimeout(() => {
                RNWalkCounter.startCounter(default_threshold, default_delay);
            }, cheatInterval)
        }
    });

    RNWalkCounter.startCounter(default_threshold, default_delay)
}

/**
 * Stops the step counter and removes event listeners.
 */
function stopCounter() {
    RNWalkCounter.stopCounter();
    if (subscription) {
        subscription.remove();
    }
    if(subscription2){
        subscription2.remove();
    }
}

export { startCounter, stopCounter };
