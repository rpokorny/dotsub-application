import React from 'react';
import './App.css';

import UploadForm from './UploadForm';
import FileList from './FileList';

export default ({model}) =>
    <main>
        <header>
            <h1>File Uploads</h1>
        </header>
        <section className="content">
            {model.errorMessage && <div className="error">{model.errorMessage}</div>}
            <UploadForm model={model.uploadModel} />
            <hr/>
            <FileList files={model.files} />
        </section>
    </main>;
