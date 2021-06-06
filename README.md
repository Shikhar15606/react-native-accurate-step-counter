
# react-native-accurate-step-counter

## Getting started

### Installation
```bash 
$ npm install react-native-accurate-step-counter
```
**or**

```bash
$ yarn add react-native-accurate-step-counter
```
### Linking

```bash 
$ react-native link react-native-accurate-step-counter
```

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
WalkEvent.removeListener('onStepRunning')	
```
  