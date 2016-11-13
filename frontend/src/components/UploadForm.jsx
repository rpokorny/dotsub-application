import React from 'react';

import { ActionPool, AsyncActionPool } from '../Pools';
import { SetUploadFilename, SetUploadTitle, SetUploadDescription } from '../Action';
import { AddFile } from '../AsyncAction';

function onFileChange(evt) {
    ActionPool.sendAction(SetUploadFilename(evt.target.value));
}

function onTitleChange(evt) {
    ActionPool.sendAction(SetUploadTitle(evt.target.value));
}

function onDescriptionChange(evt) {
    ActionPool.sendAction(SetUploadDescription(evt.target.value));
}

function onSubmit(evt) {
    evt.preventDefault();

    const file = evt.target[0].files[0];

    if (file) {
        AsyncActionPool.sendAction(AddFile(file));
    }
}

export default ({model}) =>
    <form onSubmit={onSubmit}>
        <label>
            File:
            <input type="file" value={model.filename} onChange={onFileChange} />
        </label>
        <label>
            Title:
            <input type="text" value={model.title} onChange={onTitleChange} />
        </label>
        <label>
            Description:
            <input type="text" value={model.description} onChange={onDescriptionChange} />
        </label>
        <button disabled={!model.filename} type="submit">Upload</button>
    </form>;
