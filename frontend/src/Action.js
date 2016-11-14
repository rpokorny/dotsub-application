import * as Immutable from 'immutable';
import moment from 'moment';
import FileMetadata from './model/FileMetadata';
import UploadModel from './model/UploadModel';

//helper function to remove any error message
const noError = model => model.set('errorMessage', null),
    //creates an action that just sets the given path in the model to the provided value
    setDeep = path => val => model => model.setIn(path, val),

    //create a FileMetadata record from a corresponding pojo.  This involves parsing the date
    //and invoking the record constructor
    createFileMetadata = pojo => FileMetadata(
        Object.assign({}, pojo, {
            creationDate: moment(pojo.creationDate).toDate()
        })
    );

/**
 * This module defines action contructors - functions that return Actions.
 *
 * Actions are themselves functions which take a model representing the current
 * state of the system and return a new model representing a new state for the system
 */

export function SetErrorMessage(error) {
    const message = (error instanceof Error ? error.message : error) || "Unknown Error";

    return model => model.set('errorMessage', message);
}

/**
 * Receives the full list of FileMetadata pojos in an array, and uses that to set the files list
 * in the model
 */
export function SetFiles(files) {
    //create a Immutable collection of FileMetadata models.
    //We use Immutable.Iterable, and them convert to List at the end, to avoid the expense of
    //creating an extra intermediate list (the Iterable is just a wrapper around the array)
    const models = Immutable.Iterable(files)
        .map(createFileMetadata)
        .toList();

    return model => noError(model.set('files', models));
}

/**
 * Takes a FileMetadata pojo and adds it to the list of files
 */
export function AddFile(fileMetadata) {
    const fileMetadataModel = createFileMetadata(fileMetadata), //fileMetadata as model (not pojo)
        id = fileMetadata.id;               //id of new fileMetadata

    return function(model) {
        //fileMetadatas already in the state
        const existingFiles = model.files,

            //existing fileMetadata with same id (shouldn't exist)
            existingFile = existingFiles.find(t => t.id === id);

        if (existingFile) {
            return model.set('errorMessage', 'Id conflict');
        }
        else {
            //the list of files with the new fileMetadata appended
            const newFiles = existingFiles.push(fileMetadataModel);

            return noError(model.set('files', newFiles));
        }
    };
}

/**
 * Setters for the fields in the upload form.
 */
export const SetUploadTitle = setDeep(['uploadModel', 'title']);
export const SetUploadDescription = setDeep(['uploadModel', 'description']);
export const SetUploadFilename = setDeep(['uploadModel', 'filename']);

/**
 * Reset the upload form to its empty state
 */
export function ClearUpload() {
    return model => model.set('uploadModel', UploadModel());
}
