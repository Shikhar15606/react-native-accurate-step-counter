
# react-native-accurate-step-counter

## Getting started

`$ npm install react-native-accurate-step-counter`

### Automatic Installation

`$ react-native link react-native-accurate-step-counter`


## Usage
```javascript
import StepCounter from 'react-native-accurate-step-counter';
const WalkEvent = new NativeEventEmitter({StepCounter})

// to add event listener
WalkEvent.addListener('onStepRunning', (event) => {      
      console.log(event.steps)      
})

// to start the counter
StepCounter.startCounter()

// to stop the counter 
StepCounter.stopCounter()

// to remove the event listener
StepCounter.removeListener('onStepRunning')	

```
  