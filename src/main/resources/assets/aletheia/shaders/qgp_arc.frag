uniform sampler2D texture;
uniform float time;

void main() {
    vec2 uv = gl_TexCoord[0].xy;
    vec4 color = texture2D(texture, uv);

    float pulse = 0.5 + 0.5 * sin(time * 12.0 + uv.x * 20.0);
    float r = 0.5 + 0.5 * sin(time * 6.0);
    float g = 0.5 + 0.5 * sin(time * 6.0 + 2.094);
    float b = 0.5 + 0.5 * sin(time * 6.0 + 4.188);

    vec3 rgbColor = vec3(r, g, b);
    color.rgb = mix(color.rgb, rgbColor, 0.5) + rgbColor * pulse * 0.5;

    gl_FragColor = color;
}
