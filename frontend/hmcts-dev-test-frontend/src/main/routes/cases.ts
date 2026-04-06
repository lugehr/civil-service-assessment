import { Application } from 'express';
import axios from 'axios';
import config from 'config';

const backendUrl = config.get<string>('backendUrl');

export default function (app: Application): void {
  app.get('/cases', async (req, res) => {
    try {
      const response = await axios.get(`${backendUrl}/cases`);
      res.render('cases', { cases: response.data });
    } catch (error) {
      console.error('Error fetching cases:', error);
      res.render('cases', { cases: [], error: 'Failed to load cases.' });
    }
  });

  app.get('/cases/new', (_req, res) => {
    res.render('new-case', {});
  });

  app.get('/cases/search', (req, res) => {
    const id = req.query.id as string;
    if (id?.trim()) {
      res.redirect(`/cases/${id.trim()}`);
    } else {
      res.redirect('/cases');
    }
  });

  app.post('/cases', async (req, res) => {
    const { title, dueDate } = req.body;
    const missingFields: string[] = [];
    if (!title?.trim()) missingFields.push('Title');
    if (!dueDate?.trim()) missingFields.push('Due date');

    if (missingFields.length > 0) {
      return res.render('new-case', {
        error: `The following fields are required: ${missingFields.join(', ')}`,
        values: req.body,
      });
    }

    try {
      await axios.post(`${backendUrl}/cases`, JSON.stringify(req.body), {
        headers: { 'Content-Type': 'application/json' },
      });
      res.redirect('/cases');
    } catch (error) {
      console.error('Error creating case:', error);
      let message = 'Failed to create case.';
      if (axios.isAxiosError(error)) {
        if (error.response) {
          console.error('Backend error response body:', JSON.stringify(error.response.data));
          message = `Failed to create case: ${error.response.status} ${error.response.statusText}` +
            (error.response.data?.message ? ` — ${error.response.data.message}` : '');
        } else if (error.request) {
          message = 'Failed to create case: could not reach the server.';
        }
      }
      res.render('new-case', { error: message, values: req.body });
    }
  });

  app.get('/cases/:id/status', async (req, res) => {
    try {
      const response = await axios.get(`${backendUrl}/cases/${req.params.id}`);
      res.render('edit-status', { case: response.data });
    } catch (error) {
      console.error(`Error fetching case ${req.params.id}:`, error);
      res.render('edit-status', { case: null, error: 'Case not found.' });
    }
  });

  app.post('/cases/:id/status', async (req, res) => {
    try {
      await axios.patch(`${backendUrl}/cases/${req.params.id}/status`, JSON.stringify({ status: req.body.status }), {
        headers: { 'Content-Type': 'application/json' },
      });
      res.redirect(`/cases/${req.params.id}`);
    } catch (error) {
      console.error(`Error updating case ${req.params.id}:`, error);
      res.render('edit-status', { case: { id: req.params.id }, error: 'Failed to update status.' });
    }
  });

  app.get('/cases/:id', async (req, res) => {
    try {
      const response = await axios.get(`${backendUrl}/cases/${req.params.id}`);
      res.render('case', { case: response.data });
    } catch (error) {
      console.error(`Error fetching case ${req.params.id}:`, error);
      res.render('case', { case: null, error: 'Case not found.' });
    }
  });
}
