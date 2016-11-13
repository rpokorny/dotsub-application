import * as Immutable from 'immutable';
import UploadModel from './UploadModel';

/**
 * The model that represents the overall state of the frontend.
 * Immutable.Record is sort of a meta-constructor.  The return of this call to
 * Immutable.Record is another function, which can be used to construct records with
 * the properties and default values specified here
 */
export default Immutable.Record({
    errorMessage: null,         //null if no error, string message if error
    files: Immutable.List(),    //List of FileMetadata objects
    uploadModel: UploadModel()
});
