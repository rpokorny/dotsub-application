import * as Kefir from 'kefir';
import { handleFetchErrors, fetchStream } from './utils/FetchUtils';
import * as Action from './Action';

//this gets injected by webpack
/* global API_ENTRY_URI */
const apiEntryPoint = process.env.REACT_APP_BACKEND_URI,
    handleErrors = handleFetchErrors.bind(null, Action.SetErrorMessage);

export function LoadFiles() {
    return () => handleErrors(
        fetchStream(apiEntryPoint, undefined, undefined, 'application/json').map(Action.SetFiles)
    );
}

/**
 * Create an AsyncAction that will save a new file with the name and description from
 * the Model's uploadModel.
 * @param file a native API File object representing the file to upload
 */
export function AddFile(file) {

    return function(model) {
        const formData = new FormData(),
            { uploadModel } = model;

        formData.append("title", uploadModel.title.trim());
        formData.append("description", uploadModel.description.trim());
        formData.append("file", file);

        return Kefir.concat([
            handleErrors(
                fetchStream(apiEntryPoint, 'POST', formData)
                    .map(Action.AddFile)
            ),
            Kefir.constant(Action.ClearUpload()),
        ]);
    };
}
