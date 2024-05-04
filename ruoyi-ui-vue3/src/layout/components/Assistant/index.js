const scriptContent = `
  window.addEventListener('DOMContentLoaded', (event) => {
    const script = document.createElement('script');
    script.async = true;
    script.defer = true;
    script.src = 'http://localhost:8190/api/application/embed?protocol=http&host=localhost:8190&token=a3f39ad45c8b5b8e';
    document.head.appendChild(script);
  });
`;

export const insertScript = () => {
  // Create a new script element
  const s = document.createElement('script');
  // Set the script's content
  s.textContent = scriptContent;
  // Append the script to the document body or head
  (document.head || document.body).appendChild(s);
};

