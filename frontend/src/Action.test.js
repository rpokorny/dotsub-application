import * as Immutable from 'immutable';

import * as Action from './Action';

import Model from './model/Model';
import FileMetadata from './model/FileMetadata';
import UploadModel from './model/UploadModel';

const defaultModel = Model();

describe('SetErrorMessage', function() {
    it('sets the errorMessage property to the passed in string', function() {
        const error = 'database explosion',
            retval = Action.SetErrorMessage(error)(defaultModel);

        expect(retval.errorMessage).toBe(error);
    });

    it('sets the errorMessage property to the message of the passed in error', function() {
        const error = new Error('database explosion'),
            retval = Action.SetErrorMessage(error)(defaultModel);

        expect(retval.errorMessage).toBe(error.message);
    });
});

describe('SetFiles', function() {
    it('sets the files property to an immutable list of FileMetadata records based on the input',
        function() {
            const data = [{
                    filename: 'file1.txt',
                    title: 'file 1',
                    mediaType: 'text/plain',
                    id: '1234',
                    description: 'the first file',
                    creationDate: '1990-01-01T12:00:00Z',
                    href: 'http://localhost/1'
                }, {
                    filename: 'file2.txt',
                    title: 'file 2',
                    mediaType: 'text/csv',
                    id: '1235',
                    description: 'the second file',
                    creationDate: '1990-01-01T04:00:00Z',
                    href: 'http://localhost/2'
                }],
                retval = Action.SetFiles(data)(defaultModel),
                retFiles = retval.files;

            expect(retFiles.count()).toBe(2);
            expect(retFiles).toBeInstanceOf(Immutable.List);

            const file1 = retFiles.get(0),
                file2 = retFiles.get(1);

            expect(file1).toBeInstanceOf(FileMetadata);
            expect(file2).toBeInstanceOf(FileMetadata);

            expect(file1.id).toBe(data[0].id);
            expect(file1.href).toBe(data[0].href);
            expect(file1.filename).toBe(data[0].filename);
            expect(file1.title).toBe(data[0].title);
            expect(file1.mediaType).toBe(data[0].mediaType);
            expect(file1.description).toBe(data[0].description);
            expect(file1.creationDate.getTime()).toBe(631195200000);

            expect(file2.id).toBe(data[1].id);
            expect(file2.href).toBe(data[1].href);
            expect(file2.filename).toBe(data[1].filename);
            expect(file2.title).toBe(data[1].title);
            expect(file2.mediaType).toBe(data[1].mediaType);
            expect(file2.description).toBe(data[1].description);
            expect(file2.creationDate.getTime()).toBe(631166400000);
        }
    );

    it('Overwrites any files already in the list', function() {
        const model = defaultModel.set('files', Immutable.List.of(
                FileMetadata()
            )),
            newData = [{
                filename: 'file1.txt',
                title: 'file 1',
                mediaType: 'text/plain',
                id: '1234',
                description: 'the first file',
                creationDate: '1990-01-01T12:00:00Z',
                href: 'http://localhost/1'
            }],
            retval = Action.SetFiles(newData)(model);

        expect(retval.files.count()).toBe(1);
        expect(retval.files.get(0).filename).toBe('file1.txt');
    });
});

describe('AddFile', function() {
    it('adds the data as a FileMetadata to the list', function() {
        const data = {
                filename: 'file1.txt',
                title: 'file 1',
                mediaType: 'text/plain',
                id: '1234',
                description: 'the first file',
                creationDate: '1990-01-01T12:00:00Z',
                href: 'http://localhost/1'
            },
            model = defaultModel.set('files', Immutable.List.of(
                FileMetadata({
                    filename: 'file2.txt',
                    title: 'file 2',
                    mediaType: 'text/csv',
                    id: '1235',
                    description: 'the second file',
                    creationDate: '1990-01-01T04:00:00Z',
                    href: 'http://localhost/2'
                })
            )),
            retval = Action.AddFile(data)(model);

        expect(retval.files.count()).toBe(2);
        expect(retval.files).toBeInstanceOf(Immutable.List);

        expect(retval.files.get(0)).toBe(model.files.get(0));

        const newFile = retval.files.get(1);

        expect(newFile).toBeInstanceOf(FileMetadata);

        expect(newFile.id).toBe(data.id);
        expect(newFile.href).toBe(data.href);
        expect(newFile.filename).toBe(data.filename);
        expect(newFile.title).toBe(data.title);
        expect(newFile.mediaType).toBe(data.mediaType);
        expect(newFile.description).toBe(data.description);
        expect(newFile.creationDate.getTime()).toBe(631195200000);
    });
});

describe('SetUploadTitle', function() {
    it('sets the title property of the uploadModel to the provided string', function() {
        const retval = Action.SetUploadTitle("test title")(defaultModel);

        expect(retval.uploadModel.title).toBe('test title');
    });

    it('does not change the files or errorMessage', function() {
        const retval = Action.SetUploadTitle("test title")(defaultModel);

        expect(retval.errorMessage).toBe(defaultModel.errorMessage);
        expect(retval.files).toBe(defaultModel.files);
    });
});

describe('SetUploadDescription', function() {
    it('sets the description property of the uploadModel to the provided string', function() {
        const retval = Action.SetUploadDescription("desc")(defaultModel);

        expect(retval.uploadModel.description).toBe('desc');
    });

    it('does not change the files or errorMessage', function() {
        const retval = Action.SetUploadDescription("desc")(defaultModel);

        expect(retval.errorMessage).toBe(defaultModel.errorMessage);
        expect(retval.files).toBe(defaultModel.files);
    });
});

describe('SetUploadFilename', function() {
    it('sets the filename property of the uploadModel to the provided string', function() {
        const retval = Action.SetUploadFilename("file.txt")(defaultModel);

        expect(retval.uploadModel.filename).toBe('file.txt');
    });

    it('does not change the files or errorMessage', function() {
        const retval = Action.SetUploadFilename("file.txt")(defaultModel);

        expect(retval.errorMessage).toBe(defaultModel.errorMessage);
        expect(retval.files).toBe(defaultModel.files);
    });
});

describe('ClearUpload', function() {
    it('clears the uploadModel filename, title, and description', function() {
        const model = defaultModel.set('uploadModel', UploadModel({
                filename: 'file.txt',
                title: 'file title',
                description: 'file description'
            })),
            retval = Action.ClearUpload()(model);

        expect(retval.uploadModel.filename).toBe('');
        expect(retval.uploadModel.title).toBe('');
        expect(retval.uploadModel.description).toBe('');
    });

    it('does not change the files or errorMessage', function() {
        const model = defaultModel.set('uploadModel', UploadModel({
                filename: 'file.txt',
                title: 'file title',
                description: 'file description'
            })),
            retval = Action.ClearUpload()(model);

        expect(retval.errorMessage).toBe(model.errorMessage);
        expect(retval.files).toBe(model.files);
    });
});
