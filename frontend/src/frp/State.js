/**
 * init function that creates the main state engine for the application.
 * @param initialState The initial value of the application state
 * @param actionPool The ActionPool
 * @param asyncActionStream The stream of Async Actions (eg, AsyncActionPool.stream)
 */
export default function(actionPool, asyncActionStream, initialState) {

    //For each action in the actionPool, execute it on the previous state model to
    //create the new state model
    const stateStream = actionPool.stream//.log('actions')

            //when debugging, it often helps to add log statements to your kefir streams
            .scan((model, action) => action(model), initialState)/*.log()*/,


        //For each async action on the asyncActionStream, execute the action on the state.
        //Each executed AsyncAction returns a stream of Actions, so use flatMap to combine them
        //into one stream
        executedAsyncActions = stateStream
            .sampledBy(asyncActionStream, (state, action) => ({ state, action }))
            .flatMap(({state, action }) => action(state));

    //take the Actions that result from executing AsyncActions and direct them into the
    //actionPool so that they can impact that application state
    actionPool.pool.plug(executedAsyncActions);

    //return the stream of application state values
    return stateStream;
}
