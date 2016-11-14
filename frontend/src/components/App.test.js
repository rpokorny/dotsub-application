import React from 'react';
import render from 'react-shallow-renderer';
import * as Immutable from 'immutable';

import App from './App';
import UploadForm from './UploadForm';
import FileList from './FileList';

import UploadModel from '../model/UploadModel';
import FileMetadata from '../model/FileMetadata';
import Model from '../model/Model';

const uploadModel = UploadModel(),
    files = Immutable.List.of(FileMetadata()),
    model = Model({ uploadModel, files });

it('renders without crashing with a valid model', function() {
    render(<App model={model}/>);
});

it('renders an UploadForm with the uploadModel', function() {
    const tree = render(<App model={model} />),
        section = Immutable.List(tree.props.children).find(c => c.type === 'section'),
        uploadForm = Immutable.List(section.props.children)
            .find(c => c && c.type === UploadForm);

    expect(uploadForm).toBeDefined();
    expect(uploadForm.props.model).toBe(uploadModel);
});

it('renders a FileList with the files', function() {
    const tree = render(<App model={model} />),
        section = Immutable.List(tree.props.children).find(c => c.type === 'section'),
        fileList = Immutable.List(section.props.children)
            .find(c => c && c.type === FileList);

    expect(fileList).toBeDefined();
    expect(fileList.props.files).toBe(files);
});
