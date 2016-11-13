import * as Kefir from 'kefir';
import { exceptionsToErrors } from './KefirUtils';

//the response is supposed to have an `ok` attribute but in some browsers it doesn't
function ok(resp) {
    const respOk = resp.ok,
        status = resp.status;

    if (respOk !== undefined) {
        return respOk;
    }
    else {
        return (status >= 200 && status < 300);
    }
}

/**
 * Perform an HTTP call and return a Kefir stream containing the Response or an error
 */
export function fetchStreamWithoutData(uri, method, body, contentType) {
    //use the (upcoming standard) window.fetch API to perform the HTTP call.  This returns
    //a Promise
    const headers = {
        Accept: 'application/json',
    };

    if (contentType) {
        headers['Content-Type'] = contentType;
    }

    const promise = window.fetch(uri, {
        method: method || 'GET',
        body,
        headers
    });

    return exceptionsToErrors(Kefir.fromPromise(promise)
        .flatMap(resp => ok(resp) ? Kefir.constant(resp) : Kefir.constantError(resp)));
}

/**
 * Perform an HTTP call and return a Kefir stream containing the parsed JSON result
 */
export const fetchStream = (uri, method, body, contentType) =>
    fetchStreamWithoutData(uri, method, body, contentType)
        .flatMap(resp => Kefir.fromPromise(resp.json()))/*.log()*/;

/**
 * convert any Response objects in the error stream into Error objects.
 * The return of this function has to be a stream because the fetch API's json parsing
 * is promise-based.  The stream will only have errors, and will have no values
 */
export function getResponseError(errValue) {
    //stream of error messages as stream _values_
    const errorMessageStream = errValue instanceof window.Response ?


        //values that aren't actual Errors should be HTTP responses that did "complete".
        //Try to find a server-provided error message in the
        //JSON response body.  If that doesn't work, use the HTTP status text
        Kefir.fromPromise(errValue.json())
            .map(json => json && json.error)

            //if the error property was empty/null/undefined treat as stream error
            .flatMap(errMsg => errMsg ? Kefir.constant(errMsg) : Kefir.constantError())

            //fall back to HTTP status text for any stream errors
            .flatMapErrors(() => Kefir.constant(errValue.statusText))
            .map(m => new Error(m)) :

        //some other error.  This includes Network Errors which show up as TypeErrors
        Kefir.constant(errValue);

    //convert to stream with the messages as stream _errors_
    return errorMessageStream.flatMap(Kefir.constantError);
}

/**
 * Attach common HTTP error handling to the stream
 * @param errorMapper a function to map error strings to values for inclusion in the stream
 * @param stream The stream to handle errors in
 * @return A stream of the values from `stream` and the mapped errors from `stream`
 */
export function handleFetchErrors(errorMapper, stream) {
    return stream
        .flatMapErrors(getResponseError)
        .mapErrors(errorMapper)
        .flatMapErrors(Kefir.constant);
}
