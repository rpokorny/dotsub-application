import React from 'react';
import ReactDOM from 'react-dom';

import './index.css';

import App from './components/App';

import { AsyncActionPool, ActionPool } from './Pools';
import { LoadFiles } from './AsyncAction';
import State from './frp/State';
import Model from './model/Model';

//The Kefir Property that describes the current state of the application at any
//given time
const state = State(ActionPool, AsyncActionPool.stream, Model());

//The DOM element that contains the entire JavaScript UI
const uiElement = document.getElementById('ui');

//This is where Kefir and React get connected - when the state Property changes, re-render
//the React Virtual DOM
state.onValue(function(model) {
    console.debug(model.toJS());
    ReactDOM.render(<App model={model} />, uiElement);
});

AsyncActionPool.sendAction(LoadFiles());
